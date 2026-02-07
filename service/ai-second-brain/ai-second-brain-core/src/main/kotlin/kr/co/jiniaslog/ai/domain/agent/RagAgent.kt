package kr.co.jiniaslog.ai.domain.agent

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * RAG Agent - RetrievalAugmentationAdvisor 기반 RAG 처리
 *
 * RAG 고도화:
 * 1. RewriteQueryTransformer: 질문 재작성으로 검색 정확도 향상
 * 2. VectorStoreDocumentRetriever: authorId 필터 적용한 문서 검색
 * 3. ContextualQueryAugmenter: 검색 결과를 프롬프트에 자동 병합
 * 4. MessageChatMemoryAdvisor: 대화 히스토리 자동 관리
 *
 * 동작 플로우:
 * 1. 사용자 질문 입력
 * 2. RewriteQueryTransformer가 질문 재작성 (검색 최적화)
 * 3. VectorStoreDocumentRetriever가 authorId 필터로 검색
 * 4. ContextualQueryAugmenter가 컨텍스트 병합
 * 5. ChatModel이 최종 답변 생성
 */
@Component
class RagAgent(
    @Qualifier("ragChatClient") private val chatClient: ChatClient
) {
    companion object {
        private const val SYSTEM_PROMPT = """당신은 사용자의 개인 지식 관리 시스템 AI 어시스턴트입니다.
사용자의 메모를 참조하여 정확하고 도움이 되는 답변을 제공합니다.
참조한 메모가 있다면 언급하고, 관련 메모가 없으면 솔직히 모른다고 답변하세요.
답변은 한국어로, 명확하고 간결하게 제공합니다."""
    }

    fun chat(
        message: String,
        sessionId: Long,
        authorId: Long,
        useRag: Boolean = true
    ): String {
        return chatClient.prompt()
            .system(SYSTEM_PROMPT)
            .user(message)
            .advisors { advisorSpec ->
                // Chat Memory - conversationId 설정
                advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId.toString())

                // RAG - authorId 필터 적용 (useRag=true일 때만)
                if (useRag) {
                    advisorSpec.param(
                        VectorStoreDocumentRetriever.FILTER_EXPRESSION,
                        "authorId == '$authorId'"
                    )
                }
            }
            .call()
            .content() ?: "응답을 생성할 수 없습니다."
    }
}
