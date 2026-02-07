package kr.co.jiniaslog.ai.adapter.outbound.mysql

import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageId
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageJpaRepository : JpaRepository<ChatMessage, ChatMessageId> {

    fun findBySessionIdOrderByCreatedAtAsc(sessionId: ChatSessionId): List<ChatMessage>

    fun deleteBySessionId(sessionId: ChatSessionId)
}
