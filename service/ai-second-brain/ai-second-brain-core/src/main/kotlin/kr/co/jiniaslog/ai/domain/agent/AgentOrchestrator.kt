package kr.co.jiniaslog.ai.domain.agent

import org.springframework.stereotype.Service

/**
 * Agent Orchestrator - Multi-Agent 아키텍처의 중앙 조정자
 *
 * 토큰 최적화 전략:
 * 1. Intent Router (경량 모델): 의도만 분류, 최소 토큰 사용
 * 2. RAG Agent (풀 모델): RetrievalAugmentationAdvisor가 자동으로 VectorStore 검색
 * 3. Memo Management Agent (중간 모델): Tool Calling으로 메모/폴더 관리
 */
@Service
class AgentOrchestrator(
    private val intentRouter: IntentRouterAgent,
    private val ragAgent: RagAgent,
    private val memoManagementAgent: MemoManagementAgent
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
        // 1. Intent 분류 (경량 모델)
        val intent = intentRouter.classify(message)

        // 2. Intent에 따라 적절한 Agent로 라우팅
        return when (intent) {
            Intent.QUESTION -> {
                // RetrievalAugmentationAdvisor가 자동으로 VectorStore 검색
                val response = ragAgent.chat(
                    message = message,
                    sessionId = sessionId,
                    authorId = authorId,
                    useRag = true
                )
                AgentResponse.ChatResponse(response)
            }
            Intent.MEMO_MANAGEMENT -> {
                // 메모/폴더 관리 Agent로 라우팅 (대화 히스토리 유지를 위해 sessionId 전달)
                memoManagementAgent.process(message, authorId, sessionId)
            }
            Intent.GENERAL_CHAT -> {
                // 일반 대화는 RAG 비활성화
                val response = ragAgent.chat(
                    message = message,
                    sessionId = sessionId,
                    authorId = authorId,
                    useRag = false
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
}
