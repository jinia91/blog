package kr.co.jiniaslog.user

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.annotation.Ci
import kr.co.jiniaslog.user.application.infra.TokenStore
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.application.usecase.IGetOAuthRedirectionUrl
import kr.co.jiniaslog.user.application.usecase.ILogOut
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.application.usecase.ISignInOAuthUser
import kr.co.jiniaslog.user.domain.auth.provider.Provider
import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.AuthorizationCode
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.auth.token.TokenManger
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.User
import kr.co.jiniaslog.user.domain.user.UserId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class UserUseCasesTests : TestContainerAbstractSkeleton() {
    @Nested
    inner class `인증을 위한 리디렉션 url 조회 테스트` {
        @Autowired
        lateinit var sut: IGetOAuthRedirectionUrl

        @Test
        fun `유효한 auth url 요청을 하면 유효한 응답이 온다`() {
            // given
            val command =
                IGetOAuthRedirectionUrl.Command(
                    provider = Provider.GOOGLE,
                )

            // when
            val info = sut.handle(command)

            // then
            info shouldNotBe null
            info.url shouldNotBe null
            info.url.value shouldContain "google"
        }
    }

    @Nested
    inner class `리프레시 토큰을 이용한 토큰 재발급 테스트` {
        @Autowired
        lateinit var sut: IRefreshToken

        @Autowired
        lateinit var tokenManger: TokenManger

        @Autowired
        lateinit var tokenStore: TokenStore

        @Autowired
        lateinit var userRepository: UserRepository

        @Test
        fun `포멧이 유효하지 않은 리프레시 토큰으로 갱신 요청을 하면 실패한다`() {
            // given
            val command =
                IRefreshToken.Command(
                    refreshToken = RefreshToken("12345678901"),
                )

            // when, then
            shouldThrow<IllegalArgumentException> {
                sut.handle(command)
            }
        }

        @Test
        fun `포멧이 유효한 리프레시 토큰이여도 스토어에 저장되어있지 않다면 실패한다`() {
            // given
            val refreshToken = tokenManger.generateRefreshToken(UserId(1L), setOf(Role.USER))

            val command =
                IRefreshToken.Command(
                    refreshToken = refreshToken,
                )

            // when, then
            shouldThrow<IllegalArgumentException> {
                sut.handle(command)
            }
        }

        @Test
        fun `저장된 리프레시 토큰이 존재하고, 캐시 유효기간 이내에 이전 리프레시 토큰으로 재발급 요청시 캐싱데이터를 사용한다`() {
            // given context
            val user = userRepository.save(User.newOne(NickName("test"), Email("jinia@test.com"), null))

            val accessToken = tokenManger.generateAccessToken(user.entityId, setOf(Role.USER))
            val tempRefreshToken = tokenManger.generateRefreshToken(user.entityId, setOf(Role.USER))
            tokenStore.save(user.entityId, accessToken, tempRefreshToken)
            val newRefreshToken = tokenManger.generateRefreshToken(user.entityId, setOf(Role.USER))
            tokenStore.save(user.entityId, accessToken, newRefreshToken)

            val command =
                IRefreshToken.Command(
                    refreshToken = tempRefreshToken,
                )

            // when
            val info = sut.handle(command)

            // then
            info.accessToken shouldNotBe null
            info.refreshToken shouldNotBe null

            // 갱신 체크
            info.accessToken.value shouldBe accessToken.value
            info.refreshToken.value shouldBe newRefreshToken.value
            info.refreshToken.value shouldNotBe tempRefreshToken.value

            // tearDown
            tokenStore.delete(user.entityId)
        }

        @Test
        @Ci("테스트 시간이 오래걸려서 ci에서만 확인")
        fun `저장된 리프레시 토큰이 존재하고, 캐시 유효기간 이후 리프레시토큰으로 재발급 요청시 재발급한다`() {
            // given context
            val user = userRepository.save(User.newOne(NickName("test"), Email("jinia@test.com"), null))
            val userId = user.entityId
            val accessToken = tokenManger.generateAccessToken(userId, setOf(Role.USER))
            val refreshToken = tokenManger.generateRefreshToken(userId, setOf(Role.USER))
            tokenStore.save(userId, accessToken, refreshToken)

            Thread.sleep(6000)

            val command =
                IRefreshToken.Command(
                    refreshToken = refreshToken,
                )

            // when
            val info = sut.handle(command)

            // then
            info.accessToken shouldNotBe null
            info.refreshToken shouldNotBe null

            // 갱신 체크
            info.accessToken.value shouldNotBe accessToken.value
            info.refreshToken.value shouldNotBe refreshToken.value

            // tearDown
            tokenStore.delete(userId)
        }

        @Test
        @Ci("테스트 시간이 오래걸려서 ci에서만 확인")
        fun `동일 유저아이디에 대한 동시성 요청시, 후속 요청은 캐싱을 사용한다`() {
            // given
            val user = userRepository.save(User.newOne(NickName("test"), Email("jinia@test.com"), null))
            val userId = user.entityId
            val accessToken = tokenManger.generateAccessToken(userId, setOf(Role.USER))
            val refreshToken = tokenManger.generateRefreshToken(userId, setOf(Role.USER))
            tokenStore.save(userId, accessToken, refreshToken)

            Thread.sleep(6000)

            val executorService = Executors.newFixedThreadPool(2)

            val command =
                IRefreshToken.Command(
                    refreshToken = refreshToken,
                )

            // when
            val task1 =
                CompletableFuture.supplyAsync({
                    sut.handle(command)
                }, executorService)
            val task2 =
                CompletableFuture.supplyAsync({
                    sut.handle(command)
                }, executorService)

            // then
            val result: IRefreshToken.Info = task1.get()
            val result2: IRefreshToken.Info = task2.get()

            result.accessToken.value shouldNotBe accessToken.value
            result.refreshToken.value shouldNotBe refreshToken.value

            result.accessToken.value shouldBe result2.accessToken.value
            result.refreshToken.value shouldBe result2.refreshToken.value

            // tearDown
            tokenStore.delete(userId)
        }
    }

    @Nested
    inner class `유저 인증 테스트` {
        @Autowired
        lateinit var sut: ISignInOAuthUser

        @Autowired
        lateinit var userRepository: UserRepository

        @Test
        fun `유효한 로그인 시도시 기존 유저가 없으면 회원가입 후 로그인에 성공한다`() {
            // given
            val command =
                ISignInOAuthUser.Command(
                    provider = Provider.GOOGLE,
                    code = AuthorizationCode("authCode"),
                )

            // when
            val result = sut.handle(command)

            // then
            result.accessToken shouldNotBe null

            val userList = userRepository.findAll()
            userList.size shouldBe 1
            result.nickName shouldBe userList.first().nickName
        }

        @Test
        fun `유효한 로그인 시도시 기존 유저가 있으면 로그인에 성공하며, 항상 새로운 이름으로 갱신된다`() {
            val command =
                ISignInOAuthUser.Command(
                    provider = Provider.GOOGLE,
                    code = AuthorizationCode("authCode"),
                )
            val email = Email("testUser@google.com")
            userRepository.save(User.newOne(nickName = NickName("test"), email = email, null))

            // when
            val result = sut.handle(command)

            // then
            val userList = userRepository.findAll()
            userList.size shouldBe 1
            result.nickName shouldBe NickName("testUser")
            result.email shouldBe email
        }
    }

    @Nested
    inner class `로그아웃 테스트` {
        @Autowired
        lateinit var sut: ILogOut

        @Autowired
        lateinit var tokenStore: TokenStore

        @Test
        fun `로그아웃 요청시 성공한다`() {
            // given
            val userId = UserId(1L)
            tokenStore.save(userId, AccessToken("accessToken"), RefreshToken("refresh"))

            // when
            val info = sut.handle(ILogOut.Command(userId))

            // then
            tokenStore.findByUserId(userId) shouldBe null
        }
    }

    companion object {
        private lateinit var googleOauthStub: WireMockServer

        @JvmStatic
        @BeforeAll
        fun setupWireMock() {
            googleOauthStub =
                WireMockServer(7780).apply {
                    stubFor(
                        WireMock.post(WireMock.urlEqualTo("/oauth2/token"))
                            .willReturn(
                                WireMock.aResponse()
                                    .withStatus(200)
                                    .withHeader("Content-Type", "application/json")
                                    .withHeader("Content-Length", "200")
                                    .withBody(
                                        """
                                        {
                                          "access_token": "accessToken_66037446f5e2",
                                          "expires_in": 100,
                                          "scope": "scope_70804e9d3fbe",
                                          "token_type": "tokenType_5f944efd296c",
                                          "id_token": "idToken_287fdd94b903"
                                        }
                                        """.trimIndent(),
                                    ),
                            ),
                    )

                    stubFor(
                        WireMock.get(WireMock.urlEqualTo("/oauth2/v3/userinfo"))
                            .willReturn(
                                WireMock.aResponse()
                                    .withStatus(200)
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(
                                        """
                                        {
                                          "email": "testUser@google.com",
                                          "name": "testUser",
                                          "picture": "picture_c5fb4b173dc6"
                                        }                            
                                        """.trimIndent(),
                                    ),
                            ),
                    )

                    start()
                }
        }

        @JvmStatic
        @AfterAll
        fun tearDownWireMock() {
            googleOauthStub.stop()
        }
    }
}
