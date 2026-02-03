package kr.co.jiniaslog.ai.adapter.outbound.mysql

import kr.co.jiniaslog.ai.domain.chat.AuthorId
import kr.co.jiniaslog.ai.domain.chat.ChatSession
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.ai.domain.chat.ChatSessionRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.data.repository.findByIdOrNull

@PersistenceAdapter
class ChatSessionRepositoryAdapter(
    private val jpaRepository: ChatSessionJpaRepository,
) : ChatSessionRepository {

    override fun save(chatSession: ChatSession): ChatSession {
        return jpaRepository.save(chatSession)
    }

    override fun findById(id: ChatSessionId): ChatSession? {
        return jpaRepository.findByIdOrNull(id)
    }

    override fun findAllByAuthorId(authorId: AuthorId): List<ChatSession> {
        return jpaRepository.findAllByAuthorId(authorId)
    }

    override fun delete(chatSession: ChatSession) {
        jpaRepository.delete(chatSession)
    }
}
