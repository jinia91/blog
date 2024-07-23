package kr.co.jiniaslog.admin

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.admin.adapter.inbound.http.dto.AdminCreateUserRequest
import kr.co.jiniaslog.admin.application.CreateAndLoginMockUser
import kr.co.jiniaslog.user.adapter.inbound.http.ACCESS_TOKEN_COOKIE_NAME
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.junit.jupiter.api.Test

class AdminResourceRestTests : RestTestAbstractSkeleton() {

    @Test
    fun `어드민 권한을 가진 목유저를 생성 및 로그인 요청하면 인증 토큰이 쿠키에 담긴다`() {
        // given
        val request = AdminCreateUserRequest("ADMIN", 1L)
        every {
            adminUseCases.handle(any(CreateAndLoginMockUser.Command::class))
        } returns CreateAndLoginMockUser.Info(UserId(1), Role.ADMIN)

        // when
        RestAssuredMockMvc.given()
            .contentType("application/json")
            .body(request)
            .post("/api/dev/auth/users/login")
            // then
            .then()
            .statusCode(200)
            .extract()
            .cookies()
            .let {
                it[ACCESS_TOKEN_COOKIE_NAME] shouldNotBe null
            }
    }

    @Test
    fun `로그아웃을 하면 인증 토큰이 쿠키에서 삭제된다`() {
        // when
        RestAssuredMockMvc.given()
            .contentType("application/json")
            .post("/api/dev/auth/logout")
            // then
            .then()
            .statusCode(200)
            .extract()
            .cookies()
            .let {
                it[ACCESS_TOKEN_COOKIE_NAME] shouldBe ""
            }
    }
}
