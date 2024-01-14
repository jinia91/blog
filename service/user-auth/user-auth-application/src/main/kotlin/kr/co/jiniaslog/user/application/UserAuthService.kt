package kr.co.jiniaslog.user.application

import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.user.application.infra.AuthUserLockManager
import kr.co.jiniaslog.user.application.infra.ProviderResolver
import kr.co.jiniaslog.user.application.infra.TokenStore
import kr.co.jiniaslog.user.application.infra.UserAuthTransactionHandler
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.application.usecase.IGetOAuthRedirectionUrl
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.application.usecase.ISignInOAuthUser
import kr.co.jiniaslog.user.application.usecase.UseCasesUserAuthFacade
import kr.co.jiniaslog.user.domain.auth.provider.ProviderUserInfo
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.auth.token.TokenManger
import kr.co.jiniaslog.user.domain.user.User

@UseCaseInteractor
class UserAuthService(
    private val userRepository: UserRepository,
    private val tokenStore: TokenStore,
    private val providerResolver: ProviderResolver,
    private val tokenManger: TokenManger,
    private val transactionHandler: UserAuthTransactionHandler,
    private val idempotencyUtils: AuthUserLockManager,
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
                    val accessToken = tokenManger.generateAccessToken(user.id, user.roles)
                    val refreshToken = tokenManger.generateRefreshToken(user.id, user.roles)
                    tokenStore.save(user.id, accessToken, refreshToken)

                    return@runInRepeatableReadTransaction ISignInOAuthUser.Info(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        nickName = user.nickName,
                        email = user.email,
                        roles = user.roles,
                        picUrl = providerUserInfo.picture,
                    )
                }
            return info
        }

    private fun getOrCreateUser(providerUserInfo: ProviderUserInfo): User {
        val user =
            userRepository.findByEmail(providerUserInfo.email)?.let {
                it.refreshWith(providerUserInfo)
                userRepository.save(it)
            } ?: let {
                val newUser = User.newOne(providerUserInfo)
                userRepository.save(newUser)
            }
        return user
    }

    override fun handle(command: IRefreshToken.Command): IRefreshToken.Info =
        with(command) {
            val userId = tokenManger.getUserId(refreshToken)

            if (idempotencyUtils.hasLock(userId)) {
                return idempotencyUtils.lock(userId) {
                    handleAlreadyHasLock(refreshToken)
                }
            }
            return idempotencyUtils.lock(userId) {
                handleWithoutLock(refreshToken)
            }
        }

    private fun handleAlreadyHasLock(refreshToken: RefreshToken): IRefreshToken.Info {
        require(tokenManger.validateToken(refreshToken)) { "invalid refresh token" }
        val userId = tokenManger.getUserId(refreshToken)
        val token = tokenStore.findByToken(userId)!!
        return IRefreshToken.Info(
            accessToken = token.first,
            refreshToken = token.second,
        )
    }

    private fun handleWithoutLock(refreshToken: RefreshToken): IRefreshToken.Info {
        require(tokenManger.validateToken(refreshToken)) { "invalid refresh token" }
        val userId = tokenManger.getUserId(refreshToken)
        val roles = tokenManger.getRole(refreshToken)
        val newAccessToken = tokenManger.generateAccessToken(userId, roles)
        val newRefreshToken = tokenManger.generateRefreshToken(userId, roles)
        transactionHandler.runInRepeatableReadTransaction {
            tokenStore.save(userId, newAccessToken, newRefreshToken)
        }
        return IRefreshToken.Info(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }
}
