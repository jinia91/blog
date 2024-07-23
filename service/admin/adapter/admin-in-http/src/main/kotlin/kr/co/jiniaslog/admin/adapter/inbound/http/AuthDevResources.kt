package kr.co.jiniaslog.admin.adapter.inbound.http

import kr.co.jiniaslog.admin.adapter.inbound.http.dto.AdminCreateUserRequest
import kr.co.jiniaslog.admin.adapter.inbound.http.dto.AdminCreateUserResponse
import kr.co.jiniaslog.admin.application.AdminUseCases
import kr.co.jiniaslog.admin.application.CreateAndLoginMockUser
import kr.co.jiniaslog.user.adapter.inbound.http.ACCESS_TOKEN_COOKIE_NAME
import kr.co.jiniaslog.user.domain.auth.token.TokenManger
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Profile("!prod")
@RestController
@RequestMapping("api/dev/auth")
class AuthDevResources(
    private val adminUseCases: AdminUseCases,
    private val tokenManager: TokenManger
) {
    @PostMapping("/users/login")
    fun adminCreateUsers(
        @RequestBody request: AdminCreateUserRequest
    ): ResponseEntity<AdminCreateUserResponse> {
        val command = CreateAndLoginMockUser.Command(Role.valueOf(request.role), UserId(request.id))
        val info = adminUseCases.handle(command)
        val mockAccessToken = buildTestUserToken(info.role, info.id)
        val cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, mockAccessToken)
            .path("/")
            .maxAge(36000)
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(AdminCreateUserResponse(info.id.value))
    }

    private fun buildTestUserToken(role: Role, id: UserId): String {
        return tokenManager.generateAccessToken(
            id = id,
            roles = setOf(role)
        ).value
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        val cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
            .path("/")
            .maxAge(0)
            .build()
        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }
}
