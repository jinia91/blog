package kr.co.jiniaslog.ai.usecase

interface IGetChatSessions {
    data class Query(
        val authorId: Long,
    )

    data class SessionInfo(
        val sessionId: Long,
        val title: String,
        val createdAt: java.time.LocalDateTime?,
        val updatedAt: java.time.LocalDateTime?,
    )

    operator fun invoke(query: Query): List<SessionInfo>
}
