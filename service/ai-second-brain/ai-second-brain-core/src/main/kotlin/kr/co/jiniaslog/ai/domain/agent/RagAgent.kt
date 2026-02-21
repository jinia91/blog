package kr.co.jiniaslog.ai.domain.agent

import kr.co.jiniaslog.ai.outbound.MemoQueryClient
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * RAG Agent - RetrievalAugmentationAdvisor 기반 RAG 처리
 *
 * RAG 고도화:
 * 1. RewriteQueryTransformer: 질문 재작성으로 검색 정확도 향상
 * 2. VectorStoreDocumentRetriever: authorId 필터 적용한 문서 검색
 * 3. ContextualQueryAugmenter: 검색 결과를 프롬프트에 자동 병합
 * 4. MessageChatMemoryAdvisor: 대화 히스토리 자동 관리
 * 5. L3 키워드 검색 폴백: 임베딩 검색 보완
 */
@Component
class RagAgent(
    @Qualifier("ragChatClient") private val chatClient: ChatClient,
    private val memoQueryClient: MemoQueryClient,
) {
    companion object {
        private const val SYSTEM_PROMPT_TEMPLATE = """당신은 사용자의 개인 지식 관리 시스템 AI 어시스턴트입니다.
사용자의 메모를 참조하여 정확하고 도움이 되는 답변을 제공합니다.

## 중요 규칙
1. 참조한 메모가 있다면 반드시 출처를 표시하세요: [메모: 제목]
2. 관련 메모가 없으면 솔직히 "관련 메모를 찾지 못했습니다"라고 답변하세요.
3. 답변은 한국어로, 명확하고 간결하게 제공합니다.

## 현재 시간 정보
%s

사용자가 "내일", "모레", "다음주" 등 상대적 시간으로 질문하면, 위 정보를 참고하여 해당 날짜의 일정을 찾아주세요.

## 추가 참고 자료 (키워드 검색 결과)
%s"""
    }

    internal fun buildSystemPrompt(additionalContext: String = ""): String {
        val now = LocalDateTime.now()
        val timeInfo = """현재: ${now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E) HH:mm"))}
오늘: ${now.format(DateTimeFormatter.ofPattern("MM월 dd일 (E)"))}
내일: ${now.plusDays(1).format(DateTimeFormatter.ofPattern("MM월 dd일 (E)"))}
모레: ${now.plusDays(2).format(DateTimeFormatter.ofPattern("MM월 dd일 (E)"))}"""

        return SYSTEM_PROMPT_TEMPLATE.format(timeInfo, additionalContext)
    }

    fun chat(
        message: String,
        sessionId: Long,
        authorId: Long,
    ): String {
        val keywordContext = buildKeywordContext(message, authorId)

        return chatClient.prompt()
            .system(buildSystemPrompt(keywordContext))
            .user(message)
            .advisors { advisorSpec ->
                advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId.toString())
                advisorSpec.param(
                    VectorStoreDocumentRetriever.FILTER_EXPRESSION,
                    "authorId == '$authorId'"
                )
            }
            .call()
            .content() ?: "응답을 생성할 수 없습니다."
    }

    /**
     * L3 키워드 검색으로 추가 컨텍스트를 구성합니다.
     * 임베딩 검색(L2)과 병렬로 키워드 검색(L3)을 수행하여
     * 임베딩으로 놓칠 수 있는 관련 메모를 보완합니다.
     */
    internal fun buildKeywordContext(message: String, authorId: Long): String {
        val keywords = extractKeywords(message)
        if (keywords.isEmpty()) return "없음"

        val memos = keywords.flatMap { keyword ->
            memoQueryClient.searchByKeyword(authorId, keyword, limit = 3)
        }.distinctBy { it.id }.take(5)

        if (memos.isEmpty()) return "없음"

        return memos.joinToString("\n\n") { memo ->
            "[메모: ${memo.title}] (ID: ${memo.id})\n${memo.content.take(500)}"
        }
    }

    /**
     * 메시지에서 검색에 유용한 키워드를 추출합니다.
     * 조사, 어미 등을 제거하고 핵심 단어만 추출합니다.
     */
    internal fun extractKeywords(message: String): List<String> {
        val stopWords = setOf(
            "뭐", "어떤", "무슨", "언제", "어디", "왜", "어떻게",
            "있냐", "있어", "있나", "없냐", "없어", "뭐야", "뭐있어",
            "해줘", "알려줘", "보여줘", "찾아줘", "해봐",
            "그거", "이거", "저거", "거기", "여기",
            "안녕", "고마워", "감사"
        )

        return message.split(Regex("[\\s,?!.]+"))
            .map { it.trim() }
            .filter { it.length >= 2 && it !in stopWords }
            .take(3)
    }
}
