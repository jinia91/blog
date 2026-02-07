package kr.co.jiniaslog.ai.domain.chat

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate
import kr.co.jiniaslog.shared.core.domain.IdUtils

@Entity
@Table(
    name = "chat_message",
    indexes = [
        Index(name = "idx_chat_message_session_id", columnList = "session_id"),
        Index(name = "idx_chat_message_session_cursor", columnList = "session_id, created_at, id"),
    ]
)
class ChatMessage private constructor(
    id: ChatMessageId,
    sessionId: ChatSessionId,
    role: MessageRole,
    content: String,
) : JpaAggregate<ChatMessageId>() {

    @EmbeddedId
    @AttributeOverride(column = Column(name = "id"), name = "value")
    override val entityId: ChatMessageId = id

    @AttributeOverride(column = Column(name = "session_id"), name = "value")
    val sessionId: ChatSessionId = sessionId

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: MessageRole = role

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    val content: String = content

    companion object {
        fun create(
            sessionId: ChatSessionId,
            role: MessageRole,
            content: String,
        ): ChatMessage {
            require(content.isNotBlank()) { "Content must not be blank" }
            return ChatMessage(
                id = ChatMessageId(IdUtils.idGenerator.generate()),
                sessionId = sessionId,
                role = role,
                content = content,
            )
        }

        fun from(
            id: ChatMessageId,
            sessionId: ChatSessionId,
            role: MessageRole,
            content: String,
        ): ChatMessage {
            return ChatMessage(
                id = id,
                sessionId = sessionId,
                role = role,
                content = content,
            )
        }
    }
}
