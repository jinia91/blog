package kr.co.jiniaslog.ai.adapter.outbound.mysql

import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageId
import kr.co.jiniaslog.ai.domain.chat.ChatMessageRepository
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter

@PersistenceAdapter
class ChatMessageRepositoryAdapter(
    private val jpaRepository: ChatMessageJpaRepository,
    private val queryRepository: ChatMessageQueryRepository,
) : ChatMessageRepository {

    override fun save(chatMessage: ChatMessage): ChatMessage {
        return jpaRepository.save(chatMessage)
    }

    override fun saveAll(chatMessages: List<ChatMessage>): List<ChatMessage> {
        return jpaRepository.saveAll(chatMessages)
    }

    override fun findAllBySessionId(sessionId: ChatSessionId): List<ChatMessage> {
        return jpaRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
    }

    override fun findBySessionIdWithCursor(
        sessionId: ChatSessionId,
        cursor: ChatMessageId?,
        size: Int,
    ): List<ChatMessage> {
        return queryRepository.findBySessionIdWithCursor(sessionId, cursor, size)
    }

    override fun findLastUserMessagesBySessionIds(sessionIds: List<ChatSessionId>): Map<ChatSessionId, ChatMessage?> {
        return queryRepository.findLastUserMessagesBySessionIds(sessionIds)
    }

    override fun deleteAllBySessionId(sessionId: ChatSessionId) {
        jpaRepository.deleteBySessionId(sessionId)
    }
}
