package kr.co.jiniaslog.auth

import io.kotest.assertions.throwables.shouldThrow
import kr.co.jiniaslog.WebSocketTestAbstractSkeleton
import kr.co.jiniaslog.memo.websocket.WEBSOCKET_URL
import org.junit.jupiter.api.Test
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class WebSocketAuthTest : WebSocketTestAbstractSkeleton() {
    @Test
    fun `인증되지 않은 사용자의 웹소켓 연결 시도시 실패한다`() {
        val url = String.format(WEBSOCKET_URL, port)
        subscribeFuture = CompletableFuture<Any>()

        val stompClient =
            WebSocketStompClient(
                SockJsClient(listOf<Transport>(WebSocketTransport(StandardWebSocketClient())))
            )
        stompClient.messageConverter = MappingJackson2MessageConverter()
        val headers = WebSocketHttpHeaders()

        val connectionFuture: ListenableFuture<StompSession> = stompClient.connect(
            url,
            headers,
            object : StompSessionHandlerAdapter() {}
        )

        // 래핑해야하지않나?
        shouldThrow<ExecutionException> {
            connectionFuture.completable().exceptionally {
                throw it
            }.get(2, TimeUnit.SECONDS)
        }
    }
}
