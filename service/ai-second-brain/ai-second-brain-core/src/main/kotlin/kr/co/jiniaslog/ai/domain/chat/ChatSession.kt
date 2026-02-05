package kr.co.jiniaslog.ai.domain.chat

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate
import kr.co.jiniaslog.shared.core.domain.IdUtils

@Entity
@Table(
    name = "chat_session",
    indexes = [
        Index(name = "idx_chat_session_author_id", columnList = "author_id"),
        Index(name = "idx_chat_session_author_updated", columnList = "author_id, updated_at"),
    ]
)
class ChatSession private constructor(
    id: ChatSessionId,
    authorId: AuthorId,
    title: String,
) : JpaAggregate<ChatSessionId>() {

    @EmbeddedId
    @AttributeOverride(column = Column(name = "id"), name = "value")
    override val entityId: ChatSessionId = id

    @AttributeOverride(column = Column(name = "author_id"), name = "value")
    val authorId: AuthorId = authorId

    @Column(name = "title", nullable = false)
    var title: String = title
        private set

    fun updateTitle(newTitle: String) {
        require(newTitle.isNotBlank()) { "Title must not be blank" }
        this.title = newTitle
    }

    companion object {
        const val DEFAULT_TITLE = "새 대화"

        fun create(
            authorId: AuthorId,
            title: String = DEFAULT_TITLE,
        ): ChatSession {
            return ChatSession(
                id = ChatSessionId(IdUtils.idGenerator.generate()),
                authorId = authorId,
                title = title,
            )
        }

        fun from(
            id: ChatSessionId,
            authorId: AuthorId,
            title: String,
        ): ChatSession {
            return ChatSession(
                id = id,
                authorId = authorId,
                title = title,
            )
        }
    }
}
