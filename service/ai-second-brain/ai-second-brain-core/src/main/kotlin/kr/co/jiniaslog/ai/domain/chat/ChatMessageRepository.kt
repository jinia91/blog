package kr.co.jiniaslog.ai.domain.chat

interface ChatMessageRepository {
    fun save(chatMessage: ChatMessage): ChatMessage
    fun saveAll(chatMessages: List<ChatMessage>): List<ChatMessage>
    fun findAllBySessionId(sessionId: ChatSessionId): List<ChatMessage>
    fun findBySessionIdWithCursor(
        sessionId: ChatSessionId,
        cursor: ChatMessageId?,
        size: Int
    ): List<ChatMessage>
    fun deleteAllBySessionId(sessionId: ChatSessionId)
}
