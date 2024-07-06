package kr.co.jiniaslog.user

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.shared.core.domain.vo.Url
import kr.co.jiniaslog.user.adapter.inbound.http.OAuthLoginRequest
import kr.co.jiniaslog.user.application.usecase.IGetOAuthRedirectionUrl
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.application.usecase.ISignInOAuthUser
import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.Role
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.post

class AuthUserResourceRestTests : RestTestAbstractSkeleton() {
    @Nested
    inner class `유저 인증 테스트` {
        @Test
        fun `유효한 외부 인증 제공자를 요청하면 200과 리다이렉트 URL을 반환한다`() {
            // given
            every { userUseCases.handle(any(IGetOAuthRedirectionUrl.Command::class)) } returns IGetOAuthRedirectionUrl.Info(
                Url("http://test.com")
            )

            // when
            RestAssuredMockMvc.given()
                .contentType("application/json")
                .get("/api/v1/auth/GOOGLE/url")
                // then
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("url")
                .let { url ->
                    url shouldBe "http://test.com"
                }
        }

        @Test
        fun `유효하지 않은 외부 인증 제공자를 요청하면 400을 반환한다`() {
            // given
            val invalid = "INVALID"

            // when
            RestAssuredMockMvc.given()
                .contentType("application/json")
                .get("/api/v1/auth/$invalid/url")
                // then
                .then()
                .statusCode(400)
        }

        @Test
        fun `유효한 외부인증 코드로 로그인을 시도하면 로그인에 성공한다`() {
            // given
            every { userUseCases.handle(any(ISignInOAuthUser.Command::class)) } returns ISignInOAuthUser.Info(
                nickName = NickName.UNKNOWN,
                email = Email("test@Test.com"),
                accessToken = AccessToken("test"),
                refreshToken = RefreshToken("test"),
                roles = setOf(Role.USER),
                picUrl = null
            )

            // when
            RestAssuredMockMvc.given()
                .contentType("application/json")
                .body(OAuthLoginRequest("test"))
                .post("/api/v1/auth/GOOGLE/login")
                // then
                .then()
                .statusCode(200)
                .extract()
                .cookies()
                .let { cookies ->
                    cookies["jiniaslog_access"] shouldBe "test"
                    cookies["jiniaslog_refresh"] shouldBe "test"
                }
        }

        @Test
        fun `유효한 리프레시 토큰으로 엑세스 토큰 재발급을 하면 200을 반환한다`() {
            // given
            every { userUseCases.handle(any(IRefreshToken.Command::class)) } returns IRefreshToken.Info(
                AccessToken("test2"),
                RefreshToken("test2")
            )

            // when
            RestAssuredMockMvc.given()
                .cookie("jiniaslog_refresh", "test")
                .post("/api/v1/auth/refresh")
                // then
                .then()
                .statusCode(200)
                .extract()
                .cookies()
                .let { cookies ->
                    cookies["jiniaslog_access"] shouldBe "test2"
                }
        }
    }
}
