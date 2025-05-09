package kr.co.jiniaslog.user

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.shared.core.domain.vo.Url
import kr.co.jiniaslog.user.adapter.inbound.http.dto.OAuthLoginRequest
import kr.co.jiniaslog.user.application.usecase.IGetOAuthRedirectionUrl
import kr.co.jiniaslog.user.application.usecase.ILogOut
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.application.usecase.ISignInOAuthUser
import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
                accessToken = AccessToken("test"),
                refreshToken = RefreshToken("test"),
                nickName = NickName.UNKNOWN,
                email = Email("test@Test.com"),
                roles = setOf(Role.USER),
                picUrl = null,
                userId = UserId(1L)
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
                RefreshToken("test2"),
                nickName = NickName.UNKNOWN,
                email = Email("test@Test.com"),
                roles = setOf(Role.USER),
                picUrl = null,
                userId = UserId(1L)
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

    @Nested
    inner class `유저 로그아웃 테스트` {
        @Test
        fun `유효한 유저가 로그아웃을 요청하면 200을 반환한다`() {
            // given
            every { userUseCases.handle(any(ILogOut.Command::class)) } returns ILogOut.Info()

            // when
            RestAssuredMockMvc.given()
                .cookie("jiniaslog_access", getTestUserToken())
                .cookie("jiniaslog_refresh", getTestUserToken())
                .post("/api/v1/auth/logout")
                // then
                .then()
                .statusCode(200)
                .extract()
                .cookies()
                .let { cookies ->
                    cookies["jiniaslog_access"] shouldBe ""
                    cookies["jiniaslog_refresh"] shouldBe ""
                }
        }

        @Test
        fun `유효하지 않은 유저가 로그아웃을 요청하면 401을 반환한다`() {
            // given
            every { userUseCases.handle(any(ILogOut.Command::class)) } returns ILogOut.Info()

            // when
            RestAssuredMockMvc.given()
                .cookie("jiniaslog_access", "INVALID")
                .cookie("jiniaslog_refresh", "INVALID")
                .post("/api/v1/auth/logout")
                // then
                .then()
                .statusCode(401)
        }
    }
}
