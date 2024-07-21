package kr.co.jiniaslog.user.adapter.inbound.http

import kr.co.jiniaslog.user.adapter.inbound.http.dto.OAuthLoginRequest
import kr.co.jiniaslog.user.adapter.inbound.http.dto.OAuthLoginResponse
import kr.co.jiniaslog.user.adapter.inbound.http.dto.RedirectUrlResponse
import kr.co.jiniaslog.user.application.usecase.IGetOAuthRedirectionUrl
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.application.usecase.ISignInOAuthUser
import kr.co.jiniaslog.user.application.usecase.UseCasesUserAuthFacade
import kr.co.jiniaslog.user.domain.auth.provider.Provider
import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.AuthorizationCode
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/auth")
class AuthUserResources(
    private val usecases: UseCasesUserAuthFacade,
) {
    @GetMapping("/{provider}/url")
    fun getRedirectUrl(
        @PathVariable provider: String,
    ): ResponseEntity<RedirectUrlResponse> {
        val info =
            usecases.handle(
                IGetOAuthRedirectionUrl.Command(
                    provider = Provider.valueOf(provider),
                ),
            )

        return ResponseEntity.ok()
            .body(RedirectUrlResponse(info.url.value))
    }

    @PostMapping("/{provider}/login")
    fun login(
        @PathVariable provider: String,
        @RequestBody request: OAuthLoginRequest,
    ): ResponseEntity<OAuthLoginResponse> {
        val command = ISignInOAuthUser.Command(
            provider = Provider.valueOf(provider),
            code = AuthorizationCode(request.code),
        )
        val info = usecases.handle(command)
        val response =
            OAuthLoginResponse(
                nickName = info.nickName.value,
                email = info.email.value,
                roles = info.roles.map { it.toString() }.toSet(),
                picUrl = info.picUrl?.value,
            )

        val accessCookie = buildCookieWithAccessToken(info.accessToken)

        val refreshCookie = buildCookieWithRefreshToken(info.refreshToken)

        return ResponseEntity.ok()
            .header(SET_COOKIE, accessCookie.toString())
            .header(SET_COOKIE, refreshCookie.toString())
            .body(response)
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue("jiniaslog_refresh") refreshToken: String,
    ): ResponseEntity<Void> {
        val command = IRefreshToken.Command(RefreshToken(refreshToken))
        val info = usecases.handle(command)
        val accessCookie = buildCookieWithAccessToken(info.accessToken)
        val refreshCookie = buildCookieWithRefreshToken(info.refreshToken)
        return ResponseEntity.ok()
            .header(SET_COOKIE, accessCookie.toString())
            .header(SET_COOKIE, refreshCookie.toString())
            .body(null)
    }

    private fun buildCookieWithAccessToken(accessToken: AccessToken): ResponseCookie =
        ResponseCookie.from("jiniaslog_access", accessToken.value)
            .domain("localhost")
            .path("/")
            .httpOnly(true)
            .maxAge(60 * 60)
            .secure(true)
            .sameSite("Strict")
            .build()

    private fun buildCookieWithRefreshToken(refreshToken: RefreshToken): ResponseCookie =
        ResponseCookie.from("jiniaslog_refresh", refreshToken.value)
            .domain("localhost")
            .path("/api/v1/auth/refresh")
            .httpOnly(true)
            .maxAge(60 * 60 * 24 * 7)
            .secure(true)
            .sameSite("Strict")
            .build()
}
