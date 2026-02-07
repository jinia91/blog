package kr.co.jiniaslog.ai.domain

import kr.co.jiniaslog.ai.domain.chat.AuthorId
import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageId
import kr.co.jiniaslog.ai.domain.chat.ChatSession
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.ai.domain.chat.MessageRole
import kr.co.jiniaslog.shared.core.domain.IdUtils

object ChatTestFixtures {

    fun createChatSession(
        id: Long = IdUtils.idGenerator.generate(),
        authorId: Long = 1L,
        title: String = "Test Session",
    ): ChatSession {
        return ChatSession.from(
            id = ChatSessionId(id),
            authorId = AuthorId(authorId),
            title = title,
        )
    }

    fun createChatMessage(
        id: Long = IdUtils.idGenerator.generate(),
        sessionId: Long = 1L,
        role: MessageRole = MessageRole.USER,
        content: String = "Test message content",
    ): ChatMessage {
        return ChatMessage.from(
            id = ChatMessageId(id),
            sessionId = ChatSessionId(sessionId),
            role = role,
            content = content,
        )
    }
}
