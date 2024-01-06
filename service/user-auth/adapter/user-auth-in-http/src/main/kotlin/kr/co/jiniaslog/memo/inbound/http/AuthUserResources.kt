package kr.co.jiniaslog.memo.inbound.http

import kr.co.jiniaslog.user.domain.auth.AuthorizationCode
import kr.co.jiniaslog.user.domain.auth.Provider
import kr.co.jiniaslog.user.usecase.IGetOAuthRedirectionUrl
import kr.co.jiniaslog.user.usecase.ISignInOAuthUser
import kr.co.jiniaslog.user.usecase.UserAuthUseCasesFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/auth")
class AuthUserResources(
    private val usecases: UserAuthUseCasesFacade,
) {
    @GetMapping("/{provider}/url")
    fun getRedirectUrl(
        @PathVariable provider: String,
    ): RedirectUrlResponse {
        val info =
            usecases.handle(
                IGetOAuthRedirectionUrl.Command(
                    provider = Provider.valueOf(provider),
                ),
            )
        return RedirectUrlResponse(info.url.value)
    }

    @PostMapping("/{provider}/login")
    fun login(
        @PathVariable provider: String,
        @RequestBody request: OAuthLoginRequest,
    ): LoginResponse {
        val info =
            usecases.handle(
                ISignInOAuthUser.Command(
                    provider = Provider.valueOf(provider),
                    code = AuthorizationCode(request.code),
                ),
            )

        return LoginResponse(
            accessToken = info.accessToken.value,
            refreshToken = info.refreshToken.value,
            nickName = info.nickName,
            email = info.email,
        )
    }
}
