package kr.co.jiniaslog.blog.websocket

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import kr.co.jiniaslog.WebSocketTestAbstractSkeleton
import kr.co.jiniaslog.blog.adapter.inbound.websocket.payload.UpdateArticlePayload
import kr.co.jiniaslog.blog.adapter.inbound.websocket.payload.UpdateArticleResponse
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.usecase.article.IUpdateArticleContents
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private const val WEBSOCKET_ARTICLE_UPDATE_TOPIC = "/topic/articleResponse"

private const val WEBSOCKET_ARTICLE_UPDATE = "/article/updateArticle"

private const val WEBSOCKET_URL = "ws://localhost:%d/ws"

class ArticleWebSocketHandlerTest : WebSocketTestAbstractSkeleton() {
    @BeforeEach
    fun init() {
        val url = String.format(WEBSOCKET_URL, port)
        subscribeFuture = CompletableFuture<Any>()

        val stompClient =
            WebSocketStompClient(
                SockJsClient(listOf<Transport>(WebSocketTransport(StandardWebSocketClient())))
            )
        stompClient.messageConverter = MappingJackson2MessageConverter()
        val headers = WebSocketHttpHeaders()
        headers.add("Cookie", "${PreAuthFilter.ACCESS_TOKEN_HEADER}=${getTestAdminUserToken()}")

        this.client =
            stompClient.connect(
                url, headers,
                object : StompSessionHandlerAdapter() {
                }
            ).get(2, TimeUnit.SECONDS)

        client.subscribe(
            WEBSOCKET_ARTICLE_UPDATE_TOPIC,
            object : StompFrameHandler {
                override fun getPayloadType(headers: StompHeaders): Type {
                    return UpdateArticleResponse::class.java
                }

                override fun handleFrame(headers: StompHeaders, payload: Any?) {
                    if (payload is UpdateArticleResponse) {
                        subscribeFuture.complete(payload)
                    }
                }
            }
        )
    }

    @AfterEach
    fun close() {
        client.disconnect()
    }

    @Test
    fun `유효한 게시글 내용 갱신 페이로드시 유효한 응답이 온다`() {
        // given
        val payload = UpdateArticlePayload(
            articleId = 1,
            title = "title",
            content = "content",
            thumbnailUrl = ""
        )
        every {
            articleUseCases.handle(any(IUpdateArticleContents.Command::class))
        } returns IUpdateArticleContents.Info(ArticleId(1))

        // when
        client.send(WEBSOCKET_ARTICLE_UPDATE, payload)

        // then
        val response = subscribeFuture.get(2, TimeUnit.SECONDS)
        response.shouldBeTypeOf<UpdateArticleResponse>()
        response.id shouldBe 1
    }
}
