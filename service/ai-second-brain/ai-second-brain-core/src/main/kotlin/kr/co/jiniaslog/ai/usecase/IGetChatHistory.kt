package kr.co.jiniaslog.ai.usecase

import kr.co.jiniaslog.ai.domain.chat.MessageRole

interface IGetChatHistory {
    data class Query(
        val sessionId: Long,
        val authorId: Long,
        val cursor: Long? = null, // 커서 (메시지ID, null이면 처음부터)
        val size: Int = 10, // 페이지 크기
    )

    data class MessageInfo(
        val messageId: Long,
        val role: MessageRole,
        val content: String,
        val createdAt: java.time.LocalDateTime?,
    )

    data class PagedMessages(
        val messages: List<MessageInfo>,
        val nextCursor: Long?, // 다음 페이지 커서 (null이면 마지막)
        val hasNext: Boolean,
    )

    operator fun invoke(query: Query): PagedMessages
}
