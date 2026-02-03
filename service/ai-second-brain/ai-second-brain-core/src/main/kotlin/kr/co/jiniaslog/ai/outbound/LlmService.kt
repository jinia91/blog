package kr.co.jiniaslog.ai.outbound

import kr.co.jiniaslog.ai.domain.chat.ChatMessage

data class ChatContext(
    val systemPrompt: String,
    val conversationHistory: List<ChatMessage>,
    val relevantDocuments: List<SimilarMemo>,
)

interface LlmService {
    fun chat(userMessage: String, context: ChatContext): String
    fun classifyIntent(message: String): IntentType
}

enum class IntentType {
    QUESTION,
    MEMO_CREATION,
    GENERAL_CHAT
}
