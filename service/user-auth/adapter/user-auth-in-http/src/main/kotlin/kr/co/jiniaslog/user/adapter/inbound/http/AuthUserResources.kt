package kr.co.jiniaslog.user.adapter.inbound.http

import kr.co.jiniaslog.user.adapter.inbound.http.dto.OAuthLoginRequest
import kr.co.jiniaslog.user.adapter.inbound.http.dto.OAuthLoginResponse
import kr.co.jiniaslog.user.adapter.inbound.http.dto.RedirectUrlResponse
import kr.co.jiniaslog.user.application.usecase.IGetOAuthRedirectionUrl
import kr.co.jiniaslog.user.application.usecase.ILogOut
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.application.usecase.ISignInOAuthUser
import kr.co.jiniaslog.user.application.usecase.UseCasesUserAuthFacade
import kr.co.jiniaslog.user.domain.auth.provider.Provider
import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.AuthorizationCode
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.user.UserId
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = mu.KotlinLogging.logger {}

@RestController
@RequestMapping("api/v1/auth")
class AuthUserResources(
    private val usecases: UseCasesUserAuthFacade,
) {
    @Value("\${host.domain}")
    private lateinit var domain: String

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
        @CookieValue(REFRESH_TOKEN_COOKIE_NAME) refreshToken: String,
    ): ResponseEntity<OAuthLoginResponse> {
        log.info { "refresh token: $refreshToken" }
        val command = IRefreshToken.Command(RefreshToken(refreshToken))
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

    private fun buildCookieWithAccessToken(accessToken: AccessToken): ResponseCookie =
        ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken.value)
            // fixme : domain도 주입하게 변경하기
            .domain("https://jiniaslog-backend.p-e.kr")
            .path("/")
            .httpOnly(true)
            .maxAge(ACCESS_TOKEN_COOKIE_MAX_AGE)
            .secure(true)
            .sameSite("Strict")
            .build()

    private fun buildCookieWithRefreshToken(refreshToken: RefreshToken): ResponseCookie =
        ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken.value)
            .domain("https://jiniaslog-backend.p-e.kr")
            .path("/api/v1/auth/refresh")
            .httpOnly(true)
            .maxAge(REFRESH_TOKEN_COOKIE_MAX_AGE)
            .secure(true)
            .sameSite("Strict")
            .build()

    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun logout(
        @AuthUserId userId: Long?
    ): ResponseEntity<Void> {
        require(userId != null) { "유저 아이디가 없습니다" }
        val command = ILogOut.Command(UserId(userId))
        usecases.handle(command)
        val accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
            .domain("https://jiniaslog-backend.p-e.kr")
            .path("/")
            .maxAge(0)
            .secure(true)
            .sameSite("Strict")
            .build()

        val refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
            .domain("https://jiniaslog-backend.p-e.kr")
            .path("/api/v1/auth/refresh")
            .maxAge(0)
            .secure(true)
            .sameSite("Strict")
            .build()

        return ResponseEntity.ok()
            .header(SET_COOKIE, accessCookie.toString())
            .header(SET_COOKIE, refreshCookie.toString())
            .build()
    }
}
