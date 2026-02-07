package kr.co.jiniaslog.ai.usecase

interface IGetChatSessions {
    data class Query(
        val authorId: Long,
        val cursor: Long? = null,
        val size: Int = 20,
    )

    data class PagedSessions(
        val sessions: List<SessionInfo>,
        val nextCursor: Long?,
        val hasNext: Boolean,
    )

    data class SessionInfo(
        val sessionId: Long,
        val lastMessage: String?,
        val createdAt: java.time.LocalDateTime?,
        val updatedAt: java.time.LocalDateTime?,
    )

    operator fun invoke(query: Query): PagedSessions
}
