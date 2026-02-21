package kr.co.jiniaslog.ai.domain.agent

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Compound Intent Handler - 복합 의도를 순차적으로 처리합니다.
 *
 * 하나의 메시지에 여러 의도가 포함된 경우:
 * 예: "메모하고 관련 자료 찾아줘" → MEMO_WRITE + KNOWLEDGE_QUERY
 *
 * 각 서브 의도를 순서대로 처리하고, 결과를 통합하여 반환합니다.
 */
@Component
class CompoundIntentHandler(
    private val ragAgent: RagAgent,
    private val generalChatAgent: GeneralChatAgent,
    private val memoManagementAgent: MemoManagementAgent
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 복합 의도를 순차적으로 처리합니다.
     *
     * @param message 원본 사용자 메시지
     * @param subIntents 처리할 의도 목록
     * @param sessionId 세션 ID
     * @param authorId 작성자 ID
     * @return CompoundResponse (각 서브 응답을 포함한 통합 응답)
     */
    fun process(
        message: String,
        subIntents: List<Intent>,
        sessionId: Long,
        authorId: Long
    ): AgentResponse {
        if (subIntents.isEmpty()) {
            return AgentResponse.ChatResponse("요청을 처리할 수 없습니다.")
        }

        val results = mutableListOf<AgentResponse>()

        for (intent in subIntents) {
            try {
                val response = routeToAgent(message, intent, sessionId, authorId)
                results.add(response)
            } catch (e: Exception) {
                log.error("복합 의도 처리 중 오류 발생 (intent=$intent): ${e.message}", e)
                results.add(AgentResponse.Error("${intent.name} 처리 중 오류: ${e.message}"))
            }
        }

        return mergeResponses(results)
    }

    private fun routeToAgent(
        message: String,
        intent: Intent,
        sessionId: Long,
        authorId: Long
    ): AgentResponse {
        return when (intent) {
            Intent.KNOWLEDGE_QUERY -> {
                val response = ragAgent.chat(message, sessionId, authorId)
                AgentResponse.ChatResponse(response)
            }
            Intent.MEMO_WRITE, Intent.MEMO_ORGANIZE, Intent.MEMO_SEARCH -> {
                memoManagementAgent.process(message, authorId, sessionId)
            }
            Intent.GENERAL_CHAT -> {
                val response = generalChatAgent.chat(message, sessionId)
                AgentResponse.ChatResponse(response)
            }
            Intent.COMPOUND -> {
                // COMPOUND가 재귀적으로 들어오면 안 되지만 방어적 처리
                AgentResponse.ChatResponse("요청을 처리했습니다.")
            }
        }
    }

    /**
     * 여러 AgentResponse를 하나의 통합 응답으로 합칩니다.
     */
    private fun mergeResponses(responses: List<AgentResponse>): AgentResponse {
        if (responses.size == 1) return responses.first()

        val messageParts = mutableListOf<String>()

        for (response in responses) {
            when (response) {
                is AgentResponse.ChatResponse -> messageParts.add(response.content)
                is AgentResponse.MemoCreated -> messageParts.add(response.message)
                is AgentResponse.MemoUpdated -> messageParts.add(response.message)
                is AgentResponse.MemoMoved -> messageParts.add(response.message)
                is AgentResponse.FolderCreated -> messageParts.add(response.message)
                is AgentResponse.FolderRenamed -> messageParts.add(response.message)
                is AgentResponse.FolderMoved -> messageParts.add(response.message)
                is AgentResponse.Deleted -> messageParts.add(response.message)
                is AgentResponse.MemoList -> messageParts.add(response.message)
                is AgentResponse.FolderList -> messageParts.add(response.message)
                is AgentResponse.Error -> messageParts.add("[오류] ${response.message}")
            }
        }

        return AgentResponse.ChatResponse(messageParts.joinToString("\n\n---\n\n"))
    }
}
