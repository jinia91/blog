package kr.co.jiniaslog.ai.adapter.outbound.mysql

import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageId
import kr.co.jiniaslog.ai.domain.chat.ChatMessageRepository
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@PersistenceAdapter
class ChatMessageRepositoryAdapter(
    private val jpaRepository: ChatMessageJpaRepository,
) : ChatMessageRepository {

    override fun save(chatMessage: ChatMessage): ChatMessage {
        return jpaRepository.save(chatMessage)
    }

    override fun saveAll(chatMessages: List<ChatMessage>): List<ChatMessage> {
        return jpaRepository.saveAll(chatMessages)
    }

    override fun findAllBySessionId(sessionId: ChatSessionId): List<ChatMessage> {
        return jpaRepository.findAllBySessionId(sessionId)
    }

    override fun findBySessionIdWithCursor(
        sessionId: ChatSessionId,
        cursor: ChatMessageId?,
        size: Int,
    ): List<ChatMessage> {
        val pageable = PageRequest.of(0, size + 1) // +1 for hasNext check
        return if (cursor == null) {
            jpaRepository.findBySessionIdWithoutCursor(sessionId, pageable)
        } else {
            jpaRepository.findBySessionIdWithCursor(sessionId, cursor, pageable)
        }
    }

    @Transactional
    override fun deleteAllBySessionId(sessionId: ChatSessionId) {
        jpaRepository.deleteAllBySessionId(sessionId)
    }
}
