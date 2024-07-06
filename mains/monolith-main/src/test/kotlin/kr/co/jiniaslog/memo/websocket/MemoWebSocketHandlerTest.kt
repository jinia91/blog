// package kr.co.jiniaslog.memo.websocket
//
// import kr.co.jiniaslog.memo.usecase.IUpdateMemoContents
// import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
// import org.junit.jupiter.api.Assertions.assertEquals
// import org.junit.jupiter.api.Test
// import org.mockito.BDDMockito.given
// import org.mockito.Mockito
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.test.context.SpringBootTest
// import org.springframework.boot.test.mock.mockito.MockBean
// import org.springframework.test.context.ActiveProfiles
// import java.lang.reflect.Type
// import java.util.concurrent.ArrayBlockingQueue
// import java.util.concurrent.BlockingQueue
// import java.util.concurrent.TimeUnit
// import kr.co.jiniaslog.memo.adapter.inbound.websocket.UpdateMemoPayload
// import kr.co.jiniaslog.memo.adapter.inbound.websocket.UpdateMemoResponse
// import org.springframework.messaging.converter.MappingJackson2MessageConverter
// import org.springframework.messaging.simp.stomp.StompFrameHandler
// import org.springframework.messaging.simp.stomp.StompHeaders
// import org.springframework.messaging.simp.stomp.StompSession
// import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
// import org.springframework.web.socket.messaging.WebSocketStompClient
//
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles("test")
// class MemoWebSocketHandlerTest {
//
//    @Autowired
//    private lateinit var webSocketStompClient: WebSocketStompClient
//
//    @MockBean
//    private lateinit var memoUseCases: MemoUseCasesFacade
//
//    @Test
//    fun `test handle updateMemo`() {
//        val payload = UpdateMemoPayload(
//            id = 1,
//            content = "content",
//            title = "title",
//        )
//        val command: IUpdateMemoContents.Command = payload.toCommand()
//        val expectedResponse = UpdateMemoResponse(/* 초기화 값 */)
//
//        given(memoUseCases.handle(Mockito.any(IUpdateMemoContents.Command::class.java)))
//            .willReturn(expectedResponse)
//
//        val blockingQueue: BlockingQueue<UpdateMemoResponse> = ArrayBlockingQueue(1)
//
//        val session = connectWebSocket()
//        session.subscribe("/topic/memoResponse", object : StompFrameHandler {
//            override fun getPayloadType(headers: StompHeaders): Type {
//                return UpdateMemoResponse::class.java
//            }
//
//            override fun handleFrame(headers: StompHeaders, payload: Any?) {
//                blockingQueue.offer(payload as UpdateMemoResponse)
//            }
//        })
//
//        session.send("/app/updateMemo", payload)
//
//        val response = blockingQueue.poll(3, TimeUnit.SECONDS)
//
//        assertEquals(expectedResponse, response)
//    }
//
//    private fun connectWebSocket(): StompSession {
//        webSocketStompClient.messageConverter = MappingJackson2MessageConverter()
//        val future = webSocketStompClient.connect("ws://localhost:{port}/websocket-endpoint", null, StompSessionHandlerAdapter())
//        return future.get(3, TimeUnit.SECONDS)
//    }
// }
