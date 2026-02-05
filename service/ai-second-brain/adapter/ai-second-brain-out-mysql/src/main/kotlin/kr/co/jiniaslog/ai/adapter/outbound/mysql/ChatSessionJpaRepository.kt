package kr.co.jiniaslog.ai.adapter.outbound.mysql

import kr.co.jiniaslog.ai.domain.chat.AuthorId
import kr.co.jiniaslog.ai.domain.chat.ChatSession
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import org.springframework.data.jpa.repository.JpaRepository

interface ChatSessionJpaRepository : JpaRepository<ChatSession, ChatSessionId> {

    fun findByAuthorIdOrderByUpdatedAtDesc(authorId: AuthorId): List<ChatSession>
}
