package kr.co.jiniaslog.ai.outbound

import kr.co.jiniaslog.ai.domain.chat.ChatMessage

data class ChatContext(
    val systemPrompt: String,
    val conversationHistory: List<ChatMessage>,
    val relevantDocuments: List<SimilarMemo>,
)

/**
 * @deprecated AgentOrchestrator 사용 권장.
 * Multi-Agent 아키텍처로 마이그레이션되었습니다.
 * @see kr.co.jiniaslog.ai.domain.agent.AgentOrchestrator
 */
@Deprecated("Use AgentOrchestrator instead", ReplaceWith("AgentOrchestrator"))
interface LlmService {
    fun chat(userMessage: String, context: ChatContext): String
    fun classifyIntent(message: String): IntentType
}

/**
 * @deprecated Intent enum 사용 권장
 * @see kr.co.jiniaslog.ai.domain.agent.Intent
 */
@Deprecated("Use kr.co.jiniaslog.ai.agent.Intent instead")
enum class IntentType {
    QUESTION,
    MEMO_CREATION,
    GENERAL_CHAT
}
