package kr.co.jiniaslog.ai.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.ai.adapter.inbound.http.AiResources
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.ChatRequest
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.CreateSessionRequest
import kr.co.jiniaslog.ai.domain.chat.MessageRole
import kr.co.jiniaslog.ai.usecase.IChat
import kr.co.jiniaslog.ai.usecase.ICreateChatSession
import kr.co.jiniaslog.ai.usecase.IDeleteChatSession
import kr.co.jiniaslog.ai.usecase.IGetChatHistory
import kr.co.jiniaslog.ai.usecase.IGetChatSessions
import kr.co.jiniaslog.ai.usecase.IRecommendRelatedMemos
import kr.co.jiniaslog.ai.usecase.ISyncAllMemosToEmbedding
import kr.co.jiniaslog.user.application.security.AccessTokenConfig
import kr.co.jiniaslog.user.application.security.AuthProvider
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import kr.co.jiniaslog.user.application.security.SecurityConfig
import kr.co.jiniaslog.user.domain.auth.token.TokenManager
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime

@TestConfiguration
class AiSecurityTestContextConfig {
    @Bean
    fun authProvider(): AuthProvider {
        return AuthProvider()
    }

    @Bean
    fun preAuthFilter(
        jwtTokenManager: TokenManager,
        authenticationProvider: AuthProvider,
    ): PreAuthFilter {
        return PreAuthFilter(jwtTokenManager, authenticationProvider)
    }
}

@WebMvcTest(controllers = [AiResources::class])
@Import(value = [AiSecurityTestContextConfig::class, AccessTokenConfig::class, SecurityConfig::class])
class AiResourceRestTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var tokenManager: TokenManager

    @MockkBean
    private lateinit var chatUseCase: IChat

    @MockkBean
    private lateinit var createSessionUseCase: ICreateChatSession

    @MockkBean
    private lateinit var getSessionsUseCase: IGetChatSessions

    @MockkBean
    private lateinit var getChatHistoryUseCase: IGetChatHistory

    @MockkBean
    private lateinit var recommendUseCase: IRecommendRelatedMemos

    @MockkBean
    private lateinit var syncAllUseCase: ISyncAllMemosToEmbedding

    @MockkBean
    private lateinit var deleteSessionUseCase: IDeleteChatSession

    @BeforeEach
    fun setup() {
        RestAssuredMockMvc.mockMvc(mockMvc)
    }

    private fun getTestUserToken(): String {
        return tokenManager.generateAccessToken(
            UserId(1L),
            setOf(Role.USER)
        ).value
    }

    @Test
    fun contextLoads() {
    }

    @Nested
    inner class `채팅 API 테스트` {
        @Test
        fun `인증 없이 채팅하면 401을 반환한다`() {
            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ChatRequest(sessionId = 1L, message = "안녕"))
                .post("/api/ai/chat")
                .then()
                .statusCode(401)
        }

        @Test
        fun `유효한 요청으로 채팅하면 200을 반환한다`() {
            // given
            every { chatUseCase(any()) } returns IChat.Info(
                sessionId = 1L,
                response = "안녕하세요!",
                createdMemoId = null
            )

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ChatRequest(sessionId = 1L, message = "안녕"))
                .post("/api/ai/chat")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `세션 생성 API 테스트` {
        @Test
        fun `인증 없이 세션 생성하면 401을 반환한다`() {
            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(CreateSessionRequest(title = "새 세션"))
                .post("/api/ai/sessions")
                .then()
                .statusCode(401)
        }

        @Test
        fun `유효한 요청으로 세션을 생성하면 201을 반환한다`() {
            // given
            every { createSessionUseCase(any()) } returns ICreateChatSession.Info(
                sessionId = 1L,
                title = "새 세션"
            )

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(CreateSessionRequest(title = "새 세션"))
                .post("/api/ai/sessions")
                .then()
                .statusCode(201)
        }
    }

    @Nested
    inner class `세션 목록 조회 API 테스트` {
        @Test
        fun `인증 없이 세션 목록 조회하면 401을 반환한다`() {
            RestAssuredMockMvc.given()
                .get("/api/ai/sessions")
                .then()
                .statusCode(401)
        }

        @Test
        fun `유효한 요청으로 세션 목록을 조회하면 200을 반환한다`() {
            // given
            every { getSessionsUseCase(any()) } returns listOf(
                IGetChatSessions.SessionInfo(
                    sessionId = 1L,
                    title = "세션 1",
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .get("/api/ai/sessions")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `채팅 히스토리 조회 API 테스트` {
        @Test
        fun `인증 없이 히스토리 조회하면 401을 반환한다`() {
            RestAssuredMockMvc.given()
                .get("/api/ai/sessions/1/messages")
                .then()
                .statusCode(401)
        }

        @Test
        fun `유효한 요청으로 히스토리를 조회하면 200을 반환한다`() {
            // given
            every { getChatHistoryUseCase(any()) } returns IGetChatHistory.PagedMessages(
                messages = listOf(
                    IGetChatHistory.MessageInfo(
                        messageId = 1L,
                        role = MessageRole.USER,
                        content = "안녕",
                        createdAt = LocalDateTime.now()
                    )
                ),
                nextCursor = null,
                hasNext = false
            )

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .get("/api/ai/sessions/1/messages")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `메모 추천 API 테스트` {
        @Test
        fun `인증 없이 추천 요청하면 401을 반환한다`() {
            RestAssuredMockMvc.given()
                .queryParam("query", "테스트")
                .get("/api/ai/recommend")
                .then()
                .statusCode(401)
        }

        @Test
        fun `유효한 요청으로 메모 추천을 받으면 200을 반환한다`() {
            // given
            every { recommendUseCase(any()) } returns listOf(
                IRecommendRelatedMemos.RecommendedMemo(
                    memoId = 1L,
                    title = "추천 메모",
                    contentPreview = "미리보기",
                    similarity = 0.9
                )
            )

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .queryParam("query", "테스트")
                .get("/api/ai/recommend")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `동기화 API 테스트` {
        @Test
        fun `인증 없이 동기화 요청하면 401을 반환한다`() {
            RestAssuredMockMvc.given()
                .post("/api/ai/sync")
                .then()
                .statusCode(401)
        }

        @Test
        fun `유효한 요청으로 동기화하면 200을 반환한다`() {
            // given
            every { syncAllUseCase(any()) } returns ISyncAllMemosToEmbedding.Info(syncedCount = 10)

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .post("/api/ai/sync")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `세션 삭제 API 테스트` {
        @Test
        fun `인증 없이 세션 삭제하면 401을 반환한다`() {
            RestAssuredMockMvc.given()
                .delete("/api/ai/sessions/1")
                .then()
                .statusCode(401)
        }

        @Test
        fun `유효한 요청으로 세션을 삭제하면 204를 반환한다`() {
            // given
            every { deleteSessionUseCase(any()) } returns Unit

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .delete("/api/ai/sessions/1")
                .then()
                .statusCode(204)
        }
    }

    @Nested
    inner class `추가 파라미터 테스트` {
        @Test
        fun `히스토리 조회 시 커서와 사이즈를 전달할 수 있다`() {
            // given
            every { getChatHistoryUseCase(any()) } returns IGetChatHistory.PagedMessages(
                messages = emptyList(),
                nextCursor = null,
                hasNext = false
            )

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .queryParam("cursor", 10)
                .queryParam("size", 20)
                .get("/api/ai/sessions/1/messages")
                .then()
                .statusCode(200)
        }

        @Test
        fun `메모 추천 시 memoId를 전달할 수 있다`() {
            // given
            every { recommendUseCase(any()) } returns emptyList()

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .queryParam("memoId", 5)
                .queryParam("topK", 3)
                .get("/api/ai/recommend")
                .then()
                .statusCode(200)
        }

        @Test
        fun `채팅 응답에 createdMemoId가 포함될 수 있다`() {
            // given
            every { chatUseCase(any()) } returns IChat.Info(
                sessionId = 1L,
                response = "메모가 생성되었습니다.",
                createdMemoId = 100L
            )

            // when & then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ChatRequest(sessionId = 1L, message = "메모 생성해줘"))
                .post("/api/ai/chat")
                .then()
                .statusCode(200)
        }
    }
}
