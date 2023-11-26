package kr.co.jiniaslog.memo.adapter.inbound.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.co.jiniaslog.memo.usecase.MeMoUseCasesFacade
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class MemoHandler(
    private val memoUseCases: MeMoUseCasesFacade,
) : TextWebSocketHandler() {
    private val objectMapper = jacksonObjectMapper()

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        val payload = objectMapper.readValue(message.payload, PayLoad::class.java)
        when (payload) {
            is InitMemoPayload -> handleInitMemo(session, payload)
            is CommitMemoPayload -> handleCommitMemo(session, payload)
            is GetRecommendRelatedMemoPayload -> handleGetRecommendRelatedMemo(session, payload)
            else -> {}
        }
    }

    private fun handleCommitMemo(
        session: WebSocketSession,
        payload: CommitMemoPayload,
    ) {
        val info = memoUseCases.commit(payload.toCommand())
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(info.toPayload())))
    }

    private fun handleInitMemo(
        session: WebSocketSession,
        payload: InitMemoPayload,
    ) {
        val info = memoUseCases.init(payload.toCommand())
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(info.toPayload())))
    }

    private fun handleGetRecommendRelatedMemo(
        session: WebSocketSession,
        payload: GetRecommendRelatedMemoPayload,
    ) {
        val info = memoUseCases.getRecommendRelatedMemo(payload.toCommand())
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(info.toPayload())))
    }
}
