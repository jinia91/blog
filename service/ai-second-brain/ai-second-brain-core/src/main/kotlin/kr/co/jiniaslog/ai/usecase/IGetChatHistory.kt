package kr.co.jiniaslog.ai.usecase

import kr.co.jiniaslog.ai.domain.chat.MessageRole

interface IGetChatHistory {
    data class Query(
        val sessionId: Long,
        val authorId: Long,
    )

    data class MessageInfo(
        val messageId: Long,
        val role: MessageRole,
        val content: String,
        val createdAt: java.time.LocalDateTime?,
    )

    operator fun invoke(query: Query): List<MessageInfo>
}
