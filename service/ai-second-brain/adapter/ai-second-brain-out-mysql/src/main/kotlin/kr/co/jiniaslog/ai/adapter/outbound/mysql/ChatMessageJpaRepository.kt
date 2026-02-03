package kr.co.jiniaslog.ai.adapter.outbound.mysql

import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageId
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ChatMessageJpaRepository : JpaRepository<ChatMessage, ChatMessageId> {

    @Query("SELECT m FROM ChatMessage m WHERE m.sessionId = :sessionId ORDER BY m.createdAt ASC")
    fun findAllBySessionId(sessionId: ChatSessionId): List<ChatMessage>

    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.sessionId = :sessionId")
    fun deleteAllBySessionId(sessionId: ChatSessionId)
}
