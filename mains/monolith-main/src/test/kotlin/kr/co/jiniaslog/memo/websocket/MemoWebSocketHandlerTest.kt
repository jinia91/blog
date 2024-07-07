package kr.co.jiniaslog.memo.websocket

import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import kr.co.jiniaslog.memo.adapter.inbound.websocket.UpdateMemoPayload
import kr.co.jiniaslog.memo.adapter.inbound.websocket.UpdateMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.websocket.UpdateReferencesPayload
import kr.co.jiniaslog.memo.adapter.inbound.websocket.UpdateReferencesResponse
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.usecase.IUpdateMemoContents
import kr.co.jiniaslog.memo.usecase.IUpdateMemoReferences
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private const val WEBSOCKET_MEMO_UPDATE_TOPIC = "/topic/memoResponse"
private const val WEBSOCKET_MEMO_UPDATE = "/app/updateMemo"

private const val WEBSOCKET_MEMO_REFERENCE_UPDATE_TOPIC = "/topic/memoResponse/updateReferences"
private const val WEBSOCKET_MEMO_REFERENCE_UPDATE = "/app/updateReferences"

private const val WEBSOCKET_URL = "ws://localhost:%d/memo"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MemoWebSocketHandlerTest {
    @Value("\${local.server.port}")
    private val port = 0

    @MockkBean
    private lateinit var memoUseCases: MemoUseCasesFacade

    private lateinit var testClient: StompSession

    private lateinit var subscribeFuture: CompletableFuture<Any>

    @BeforeEach
    fun init() {
        val url = String.format(WEBSOCKET_URL, port)
        subscribeFuture = CompletableFuture<Any>()

        val stompClient =
            WebSocketStompClient(
                SockJsClient(listOf<Transport>(WebSocketTransport(StandardWebSocketClient())))
            )
        stompClient.messageConverter = MappingJackson2MessageConverter()
        this.testClient =
            stompClient.connect(
                url, WebSocketHttpHeaders(),
                object : StompSessionHandlerAdapter() {
                }
            ).get(2, TimeUnit.SECONDS)

        testClient.subscribe(
            WEBSOCKET_MEMO_UPDATE_TOPIC,
            object : StompFrameHandler {
                override fun getPayloadType(headers: StompHeaders): Type {
                    return UpdateMemoResponse::class.java
                }

                override fun handleFrame(headers: StompHeaders, payload: Any?) {
                    if (payload is UpdateMemoResponse) {
                        subscribeFuture.complete(payload)
                    }
                }
            }
        )

        testClient.subscribe(
            WEBSOCKET_MEMO_REFERENCE_UPDATE_TOPIC,
            object : StompFrameHandler {
                override fun getPayloadType(headers: StompHeaders): Type {
                    return UpdateReferencesResponse::class.java
                }

                override fun handleFrame(headers: StompHeaders, payload: Any?) {
                    if (payload is UpdateReferencesResponse) {
                        subscribeFuture.complete(payload)
                    }
                }
            }
        )
    }

    @AfterEach
    fun close() {
        testClient.disconnect()
    }

    @Test
    fun `유효한 메모 내용 갱신 커맨드시 유효한 응답이 온다`() {
        // given
        every { memoUseCases.handle(any(IUpdateMemoContents.Command::class)) } returns IUpdateMemoContents.Info(
            id = MemoId(1)
        )

        // when
        val updateMemoPayload = UpdateMemoPayload(
            id = 1,
            content = "content",
            title = "title"
        )
        testClient.send(WEBSOCKET_MEMO_UPDATE, updateMemoPayload)

        // then
        val response = subscribeFuture.get(3, TimeUnit.SECONDS)
        response.shouldNotBeNull()
        (response is UpdateMemoResponse) shouldBe true
        response as UpdateMemoResponse
        response.id shouldNotBe null
        response.id shouldBe 1
    }

    @Test
    fun `유효하지 않은 메모 내용 갱신 커맨드시 에러 응답이 온다`() {
        // todo : 에러핸들링 구현
    }

    @Test
    fun `인증되지 않은 사용자의 메모 내용 갱신 커맨드시 에러 응답이 온다`() {
        // todo : 인증핸들링 구현
    }

    @Test
    fun `인가되지 않은 사용자의 메모 내용 갱신 커맨드시 에러 응답이 온다`() {
        // todo : 인가핸들링 구현
    }

    @Test
    fun `유효한 메모 참조 추가 커맨드시 유효한 응답이 온다`() {
        // given
        every {
            memoUseCases.handle(any(IUpdateMemoReferences.Command.AddReference::class))
        } returns IUpdateMemoReferences.Info(
            id = MemoId(1)
        )

        val payload = UpdateReferencesPayload(
            id = 1,
            references = listOf(2, 3)
        )

        // when
        testClient.send(WEBSOCKET_MEMO_REFERENCE_UPDATE, payload)

        // then
        val response = subscribeFuture.get(3, TimeUnit.SECONDS)
        response.shouldNotBeNull()
        (response is UpdateReferencesResponse) shouldBe true
        response as UpdateReferencesResponse
        response.id shouldNotBe null
        response.id shouldBe 1
    }
}