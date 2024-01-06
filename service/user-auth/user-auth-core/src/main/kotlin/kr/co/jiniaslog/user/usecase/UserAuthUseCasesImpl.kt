package kr.co.jiniaslog.user.usecase

import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.user.domain.auth.ProviderResolver
import kr.co.jiniaslog.user.domain.auth.TokenGenerator
import kr.co.jiniaslog.user.domain.user.User
import kr.co.jiniaslog.user.domain.user.UserRepository

@UseCaseInteractor
class UserAuthUseCasesImpl(
    private val userRepository: UserRepository,
    private val providerResolver: ProviderResolver,
    private val tokenGenerator: TokenGenerator,
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
        val user =
            userRepository.findByEmail(providerUserInfo.email)?.let {
                it.renewNickName(providerUserInfo)
                userRepository.save(it)
            } ?: let {
                val newUser = User.newOne(providerUserInfo)
                userRepository.save(newUser)
            }
        return ISignInOAuthUser.Info(
            accessToken = tokenGenerator.generateAccessToken(user.id, user.role),
            refreshToken = tokenGenerator.generateRefreshToken(user.id, user.role),
            nickName = user.nickName,
            email = user.email,
            role = user.role,
        )
    }
}
