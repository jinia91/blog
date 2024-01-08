package kr.co.jiniaslog.user.usecase

import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.user.domain.auth.ProviderResolver
import kr.co.jiniaslog.user.domain.auth.TokenGenerator
import kr.co.jiniaslog.user.domain.auth.TokenStore
import kr.co.jiniaslog.user.domain.user.User
import kr.co.jiniaslog.user.domain.user.UserRepository
import kr.co.jiniaslog.user.infra.UserAuthTransactionHandler

@UseCaseInteractor
class UserAuthUseCasesImpl(
    private val userRepository: UserRepository,
    private val tokenStore: TokenStore,
    private val providerResolver: ProviderResolver,
    private val tokenGenerator: TokenGenerator,
    private val transactionHandler: UserAuthTransactionHandler,
    private val IdempotencyUtils: AuthUserLockUtils,
) : UserAuthUseCasesFacade {
    override fun handle(command: IGetOAuthRedirectionUrl.Command): IGetOAuthRedirectionUrl.Info {
        val provider = command.provider
        require(providerResolver.isSupported(provider)) { "not supported provider. provider: $provider" }
        val providerAdapter = providerResolver.resolve(provider)
        val redirectionUrl = providerAdapter.getLoginUrl()
        return IGetOAuthRedirectionUrl.Info(redirectionUrl)
    }

    override fun handle(command: ISignInOAuthUser.Command): ISignInOAuthUser.Info {
        val provider = command.provider
        val providerAdapter = providerResolver.resolve(provider)
        val providerUserInfo = providerAdapter.getUserInfo(command.code)

        val info =
            transactionHandler.runInRepeatableReadTransaction {
                val user =
                    userRepository.findByEmail(providerUserInfo.email)?.let {
                        it.renewNickName(providerUserInfo)
                        userRepository.save(it)
                    } ?: let {
                        val newUser = User.newOne(providerUserInfo)
                        userRepository.save(newUser)
                    }

                val accessToken = tokenGenerator.generateAccessToken(user.id, user.roles)
                val refreshToken = tokenGenerator.generateRefreshToken(user.id, user.roles)
                tokenStore.save(user.id, accessToken, refreshToken)
                ISignInOAuthUser.Info(
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

    override fun handle(command: IRefreshToken.Command): IRefreshToken.Info {
        val userId = tokenGenerator.getUserId(command.refreshToken.value)

        if (IdempotencyUtils.hasLock(userId)) {
            return IdempotencyUtils.lock(userId) {
                handleAlreadyHasLock(command)
            }
        }
        return IdempotencyUtils.lock(userId) {
            handleWithoutLock(command)
        }
    }

    private fun handleAlreadyHasLock(command: IRefreshToken.Command): IRefreshToken.Info {
        val refreshToken = command.refreshToken
        require(tokenGenerator.validateToken(refreshToken.value)) { "invalid refresh token" }
        val userId = tokenGenerator.getUserId(refreshToken.value)
        val token = tokenStore.findByToken(userId)!!
        return IRefreshToken.Info(
            accessToken = token.first,
            refreshToken = token.second,
        )
    }

    private fun handleWithoutLock(command: IRefreshToken.Command): IRefreshToken.Info {
        val refreshToken = command.refreshToken
        require(tokenGenerator.validateToken(refreshToken.value)) { "invalid refresh token" }
        val userId = tokenGenerator.getUserId(refreshToken.value)
        val roles = tokenGenerator.getRole(refreshToken.value)
        val newAccessToken = tokenGenerator.generateAccessToken(userId, roles)
        val newRefreshToken = tokenGenerator.generateRefreshToken(userId, roles)
        transactionHandler.runInRepeatableReadTransaction {
            tokenStore.save(userId, newAccessToken, newRefreshToken)
        }
        return IRefreshToken.Info(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }
}
