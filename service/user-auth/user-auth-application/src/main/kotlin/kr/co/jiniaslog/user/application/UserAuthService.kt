package kr.co.jiniaslog.user.application

import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.user.application.infra.AuthTokenInfo
import kr.co.jiniaslog.user.application.infra.AuthUserLockManager
import kr.co.jiniaslog.user.application.infra.ProviderResolver
import kr.co.jiniaslog.user.application.infra.TokenStore
import kr.co.jiniaslog.user.application.infra.UserAuthTransactionHandler
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.application.usecase.ICheckUserExisted
import kr.co.jiniaslog.user.application.usecase.IGetOAuthRedirectionUrl
import kr.co.jiniaslog.user.application.usecase.IGetUserInfo
import kr.co.jiniaslog.user.application.usecase.ILogOut
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.application.usecase.IRetrieveAdminUserIds
import kr.co.jiniaslog.user.application.usecase.ISignInOAuthUser
import kr.co.jiniaslog.user.application.usecase.UseCasesUserAuthFacade
import kr.co.jiniaslog.user.domain.auth.provider.ProviderUserInfo
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.auth.token.TokenManager
import kr.co.jiniaslog.user.domain.user.User
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private val log = KotlinLogging.logger {}

@UseCaseInteractor
class UserAuthService(
    private val userRepository: UserRepository,
    private val tokenStore: TokenStore,
    private val providerResolver: ProviderResolver,
    private val tokenManager: TokenManager,
    private val transactionHandler: UserAuthTransactionHandler,
    private val idempotencyLockManager: AuthUserLockManager,
) : UseCasesUserAuthFacade {
    override fun handle(command: IGetOAuthRedirectionUrl.Command): IGetOAuthRedirectionUrl.Info =
        with(command) {
            require(providerResolver.isSupported(provider)) { "not supported provider. provider: $provider" }
            val providerAdapter = providerResolver.resolve(provider)
            val loginUrl = providerAdapter.getLoginUrl()
            return IGetOAuthRedirectionUrl.Info(loginUrl)
        }

    override fun handle(command: ISignInOAuthUser.Command): ISignInOAuthUser.Info =
        with(command) {
            val providerAdapter = providerResolver.resolve(provider)
            val providerUserInfo = providerAdapter.getUserInfo(code)

            val info =
                transactionHandler.runInRepeatableReadTransaction {
                    val user = getOrCreateUser(providerUserInfo)
                    val accessToken = tokenManager.generateAccessToken(user.entityId, user.roles)
                    val refreshToken = tokenManager.generateRefreshToken(user.entityId, user.roles)
                    tokenStore.save(user.entityId, accessToken, refreshToken)

                    return@runInRepeatableReadTransaction ISignInOAuthUser.Info(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        nickName = user.nickName,
                        email = user.email,
                        roles = user.roles,
                        picUrl = providerUserInfo.picture,
                        userId = user.entityId,
                    )
                }
            return info
        }

    private fun getOrCreateUser(providerUserInfo: ProviderUserInfo): User =
        userRepository.findByEmail(providerUserInfo.email)?.let {
            it.update(providerUserInfo.nickName, providerUserInfo.picture)
            userRepository.save(it)
        } ?: let {
            val newUser = User.newOne(providerUserInfo.nickName, providerUserInfo.email, providerUserInfo.picture)
            userRepository.save(newUser)
        }

    override fun handle(command: IRefreshToken.Command): IRefreshToken.Info =
        with(command) {
            require(tokenManager.validateToken(refreshToken)) { "리프레시 토큰이 유효하지 않습니다" }
            val userId = tokenManager.extractUserId(refreshToken)
            val storedAuthToken = tokenStore.findByUserId(userId)
            validateRefreshToken(storedAuthToken, refreshToken)
            val user = userRepository.findById(userId)
                ?: throw IllegalStateException("유저가 존재하지 않습니다. userId: $userId")

            val isRecentlyIssued = storedAuthToken.oldRefreshToken == refreshToken
            if (isRecentlyIssued) {
                log.info { "이미 발급된 토큰이 있습니다. userId: $userId" }
                return IRefreshToken.Info(
                    accessToken = storedAuthToken.accessToken,
                    refreshToken = storedAuthToken.newRefreshToken,
                    nickName = user.nickName,
                    email = user.email,
                    roles = user.roles,
                    picUrl = user.picUrl,
                    userId = user.entityId,
                )
            }

            return idempotencyLockManager.lock(
                userId = userId,
                timeOutSeconds = 10,
                block = {
                    generateNewAuthTokens(refreshToken, user)
                        .also { log.info { "refreshed auth tokens. userId: $userId" } }
                },
                forIdempotencyFallback = {
                    log.warn {
                        """
                            |이미 락이 있습니다. userId: $userId
                            |refreshToken: $refreshToken
                            |중복 요청이 의심됩니다!
                        """.trimMargin()
                    }
                    reTryGetAuthTokensInCache(refreshToken, user)
                },
            )
        }

    private fun generateNewAuthTokens(
        refreshToken: RefreshToken,
        user: User,
    ): IRefreshToken.Info {
        val foundAuthTokens = tokenStore.findByUserId(user.entityId)
        validateRefreshToken(foundAuthTokens, refreshToken)
        val roles = tokenManager.getRole(refreshToken)
        val newAccessToken = tokenManager.generateAccessToken(user.entityId, roles)
        val newRefreshToken = tokenManager.generateRefreshToken(user.entityId, roles)
        transactionHandler.runInRepeatableReadTransaction {
            tokenStore.save(user.entityId, newAccessToken, newRefreshToken)
        }
        return IRefreshToken.Info(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            nickName = user.nickName,
            email = user.email,
            roles = user.roles,
            picUrl = user.picUrl,
            userId = user.entityId,
        )
    }

    private fun reTryGetAuthTokensInCache(
        refreshToken: RefreshToken,
        user: User,
    ): IRefreshToken.Info {
        val tokens = tokenStore.findByUserId(user.entityId)
        validateRefreshToken(tokens, refreshToken)

        return IRefreshToken.Info(
            accessToken = tokens.accessToken,
            refreshToken = tokens.newRefreshToken,
            nickName = user.nickName,
            email = user.email,
            roles = user.roles,
            picUrl = user.picUrl,
            userId = user.entityId,
        )
    }

    @OptIn(ExperimentalContracts::class)
    private fun validateRefreshToken(
        foundAuthTokens: AuthTokenInfo?,
        refreshToken: RefreshToken,
    ) {
        contract { returns() implies (foundAuthTokens != null) }
        requireNotNull(foundAuthTokens) { "refresh token should be managed in store" }
        require(
            foundAuthTokens.newRefreshToken == refreshToken ||
                foundAuthTokens.oldRefreshToken == refreshToken,
        ) { "invalid refresh token" }
    }

    override fun handle(command: ICheckUserExisted.Command): Boolean {
        val user = userRepository.findById(command.id)
        return user != null
    }

    override fun handle(command: ILogOut.Command): ILogOut.Info {
        tokenStore.delete(command.userId)
        return ILogOut.Info()
    }

    override fun handle(query: IRetrieveAdminUserIds.Query): IRetrieveAdminUserIds.Info {
        val adminUsers = userRepository.findAdminUsers()
        return IRetrieveAdminUserIds.Info(adminUsers.map { it.entityId })
    }

    override fun handle(query: IGetUserInfo.Query): IGetUserInfo.Info {
        val user = userRepository.findById(query.userId)
            ?: throw IllegalStateException("user not found. userId: ${query.userId}")
        return IGetUserInfo.Info(
            id = user.entityId,
            name = user.nickName.value,
            profileImageUrl = user.picUrl?.value,
        )
    }
}
