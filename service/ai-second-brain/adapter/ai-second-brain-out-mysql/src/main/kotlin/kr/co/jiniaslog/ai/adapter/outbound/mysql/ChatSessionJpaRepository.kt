package kr.co.jiniaslog.ai.adapter.outbound.mysql

import kr.co.jiniaslog.ai.domain.chat.AuthorId
import kr.co.jiniaslog.ai.domain.chat.ChatSession
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatSessionJpaRepository : JpaRepository<ChatSession, ChatSessionId> {

    @Query("SELECT s FROM ChatSession s WHERE s.authorId = :authorId ORDER BY s.updatedAt DESC")
    fun findAllByAuthorId(authorId: AuthorId): List<ChatSession>
}
