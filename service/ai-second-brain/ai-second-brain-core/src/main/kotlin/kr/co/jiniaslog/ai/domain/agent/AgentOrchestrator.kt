package kr.co.jiniaslog.ai.domain.agent

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Service

/**
 * Agent Orchestrator - Multi-Agent 아키텍처의 중앙 조정자
 *
 * 토큰 최적화 전략:
 * 1. Intent Router (경량 모델): 의도만 분류, 최소 토큰 사용 + 대화 히스토리로 맥락 유지
 * 2. RAG Agent (풀 모델): RetrievalAugmentationAdvisor가 자동으로 VectorStore 검색
 * 3. General Chat Agent (경량 모델): 일반 대화 처리, Memory만 사용
 * 4. Memo Management Agent (중간 모델): Tool Calling으로 메모/폴더 관리
 */
@Service
class AgentOrchestrator(
    private val intentRouter: IntentRouterAgent,
    private val ragAgent: RagAgent,
    private val generalChatAgent: GeneralChatAgent,
    private val memoManagementAgent: MemoManagementAgent,
    private val chatMemory: ChatMemory
) {
    /**
     * 메시지를 처리하고 적절한 Agent로 라우팅합니다.
     *
     * @param message 사용자 메시지
     * @param sessionId 세션 ID (Chat Memory의 conversationId로 사용됨)
     * @param authorId 작성자 ID
     * @return AgentResponse
     */
    fun process(
        message: String,
        sessionId: Long,
        authorId: Long
    ): AgentResponse {
        // 1. 최근 대화 히스토리를 가져와서 Intent Router에 전달 (맥락 기반 분류)
        val conversationHistory = buildConversationHistory(sessionId.toString())

        // 2. Intent 분류 (경량 모델 + 대화 맥락)
        val intent = intentRouter.classify(message, conversationHistory)

        // 3. Intent에 따라 적절한 Agent로 라우팅
        return when (intent) {
            Intent.QUESTION -> {
                val response = ragAgent.chat(
                    message = message,
                    sessionId = sessionId,
                    authorId = authorId
                )
                AgentResponse.ChatResponse(response)
            }
            Intent.MEMO_MANAGEMENT -> {
                memoManagementAgent.process(message, authorId, sessionId)
            }
            Intent.GENERAL_CHAT -> {
                val response = generalChatAgent.chat(
                    message = message,
                    sessionId = sessionId
                )
                AgentResponse.ChatResponse(response)
            }
        }
    }

    /**
     * Intent만 분류합니다 (테스트/디버깅용)
     */
    fun classifyIntent(message: String): Intent {
        return intentRouter.classify(message)
    }

    /**
     * ChatMemory에서 최근 대화 히스토리를 가져와서 텍스트로 변환합니다.
     * IntentRouter가 후속 메시지의 맥락을 파악할 수 있도록 합니다.
     */
    private fun buildConversationHistory(conversationId: String): String {
        val messages = try {
            chatMemory.get(conversationId).takeLast(6)
        } catch (e: Exception) {
            emptyList()
        }
        if (messages.isEmpty()) return ""

        return messages.joinToString("\n") { msg ->
            when (msg) {
                is UserMessage -> "사용자: ${msg.text ?: ""}"
                is AssistantMessage -> "어시스턴트: ${(msg.text ?: "").take(200)}"
                else -> ""
            }
        }.trim()
    }
}
