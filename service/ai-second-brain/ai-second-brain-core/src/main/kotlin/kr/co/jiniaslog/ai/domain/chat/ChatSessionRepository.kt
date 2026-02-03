package kr.co.jiniaslog.ai.domain.chat

interface ChatSessionRepository {
    fun save(chatSession: ChatSession): ChatSession
    fun findById(id: ChatSessionId): ChatSession?
    fun findAllByAuthorId(authorId: AuthorId): List<ChatSession>
    fun delete(chatSession: ChatSession)
}
