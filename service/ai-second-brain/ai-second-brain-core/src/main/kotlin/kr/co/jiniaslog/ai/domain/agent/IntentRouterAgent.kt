package kr.co.jiniaslog.ai.domain.agent

import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class IntentRouterAgent(
    @Qualifier("lightweightChatClient") private val chatClient: ChatClient
) {
    companion object {
        private const val ROUTER_PROMPT = """사용자 메시지의 의도를 분류하세요.

## 분류 기준

KNOWLEDGE_QUERY (지식 검색/질문):
- 이미 저장된 지식/메모에 대해 질문하거나 검색하는 경우
- 질문 어미: ~냐, ~니, ~야?, ~어?, ~나?, ~까?, ~뭐야, ~있어?, ~뭐있어, ~알려줘, ~뭐였지, ~어떻게 됐더라
- 예시:
  "내일 뭐있냐" → KNOWLEDGE_QUERY
  "회의 언제야?" → KNOWLEDGE_QUERY
  "약속 뭐있어?" → KNOWLEDGE_QUERY
  "지난주에 뭐 메모했지?" → KNOWLEDGE_QUERY
  "프로젝트 관련 내용 알려줘" → KNOWLEDGE_QUERY
  "JVM 관련 내용 찾아봐" → KNOWLEDGE_QUERY

MEMO_WRITE (메모 생성/수정):
- 새로운 정보를 기록하거나 기존 메모를 수정하는 경우
- 진술 어미: ~있다, ~이다, ~해야함, ~한다, ~했다, ~할거다
- 명령 어미: ~기록해, ~저장해, ~적어줘, ~메모해, ~노트해, ~수정해, ~고쳐줘, ~변경해
- 예시:
  "5시에 약속있다" → MEMO_WRITE
  "내일 회의다" → MEMO_WRITE
  "우유 사야함" → MEMO_WRITE
  "메모 수정해줘" → MEMO_WRITE
  "제목 바꿔줘" → MEMO_WRITE
  "오늘 배운 내용 정리해서 저장해" → MEMO_WRITE

MEMO_ORGANIZE (폴더/메모 정리):
- 폴더 생성/삭제/이름변경, 메모를 폴더로 이동, 정리하는 경우
- 예시:
  "새 폴더 만들어줘" → MEMO_ORGANIZE
  "메모 폴더로 정리해" → MEMO_ORGANIZE
  "폴더 이름 바꿔줘" → MEMO_ORGANIZE
  "폴더 삭제해줘" → MEMO_ORGANIZE
  "메모 다른 폴더로 옮겨줘" → MEMO_ORGANIZE
  "메모 삭제해줘" → MEMO_ORGANIZE

MEMO_SEARCH (메모 목록/검색):
- 메모나 폴더의 목록을 조회하거나 특정 메모를 찾는 경우
- 예시:
  "메모 목록 보여줘" → MEMO_SEARCH
  "폴더 목록 보여줘" → MEMO_SEARCH
  "어떤 메모들 있어?" → MEMO_SEARCH
  "전체 메모 보여줘" → MEMO_SEARCH

GENERAL_CHAT (일반 대화):
- 인사, 감사, 잡담, 시스템과 관련 없는 대화
- 예시:
  "안녕" → GENERAL_CHAT
  "고마워" → GENERAL_CHAT
  "뭐해?" → GENERAL_CHAT
  "오늘 날씨 어때?" → GENERAL_CHAT

COMPOUND (복합 의도):
- 하나의 메시지에 2개 이상의 의도가 포함된 경우
- 예시:
  "메모하고 관련 자료 찾아줘" → COMPOUND[MEMO_WRITE,KNOWLEDGE_QUERY]
  "폴더 만들고 메모 옮겨줘" → COMPOUND[MEMO_ORGANIZE,MEMO_ORGANIZE]
  "이거 저장하고 비슷한 메모 있나 찾아봐" → COMPOUND[MEMO_WRITE,KNOWLEDGE_QUERY]

## 핵심 구분법
- 정보를 물어보는 것 = KNOWLEDGE_QUERY
- 새로운 정보를 기록/수정하는 것 = MEMO_WRITE
- 폴더/메모를 정리/이동/삭제하는 것 = MEMO_ORGANIZE
- 목록을 조회하는 것 = MEMO_SEARCH
- 인사/잡담 = GENERAL_CHAT
- 두 가지 이상이 섞인 것 = COMPOUND[의도1,의도2]

반드시 위 분류 중 하나만 응답하세요. COMPOUND인 경우 COMPOUND[의도1,의도2] 형식으로 응답하세요.

메시지: %s

의도:"""

        private const val ROUTER_PROMPT_WITH_HISTORY = """사용자 메시지의 의도를 분류하세요.

## 최근 대화 맥락
%s

## 분류 기준

KNOWLEDGE_QUERY (지식 검색/질문):
- 이미 저장된 지식/메모에 대해 질문하거나 검색하는 경우
- 질문 어미: ~냐, ~니, ~야?, ~어?, ~나?, ~까?, ~뭐야, ~있어?, ~뭐있어, ~알려줘, ~뭐였지, ~어떻게 됐더라
- 예시:
  "내일 뭐있냐" → KNOWLEDGE_QUERY
  "회의 언제야?" → KNOWLEDGE_QUERY
  "프로젝트 관련 내용 알려줘" → KNOWLEDGE_QUERY
  "JVM 관련 내용 찾아봐" → KNOWLEDGE_QUERY

MEMO_WRITE (메모 생성/수정):
- 새로운 정보를 기록하거나 기존 메모를 수정하는 경우
- 진술 어미: ~있다, ~이다, ~해야함, ~한다, ~했다, ~할거다
- 명령 어미: ~기록해, ~저장해, ~적어줘, ~메모해, ~노트해, ~수정해, ~고쳐줘, ~변경해
- 예시:
  "5시에 약속있다" → MEMO_WRITE
  "내일 회의다" → MEMO_WRITE
  "오늘 배운 내용 정리해서 저장해" → MEMO_WRITE

MEMO_ORGANIZE (폴더/메모 정리):
- 폴더 생성/삭제/이름변경, 메모를 폴더로 이동, 정리하는 경우
- 예시:
  "새 폴더 만들어줘" → MEMO_ORGANIZE
  "메모 정리해줘" → MEMO_ORGANIZE
  "메모 삭제해줘" → MEMO_ORGANIZE

MEMO_SEARCH (메모 목록/검색):
- 메모나 폴더의 목록을 조회하거나 특정 메모를 찾는 경우
- 예시:
  "메모 목록 보여줘" → MEMO_SEARCH
  "어떤 메모들 있어?" → MEMO_SEARCH

GENERAL_CHAT (일반 대화):
- 인사, 감사, 잡담, 시스템과 관련 없는 대화
- 예시:
  "안녕" → GENERAL_CHAT
  "고마워" → GENERAL_CHAT

COMPOUND (복합 의도):
- 하나의 메시지에 2개 이상의 의도가 포함된 경우
- 예시:
  "메모하고 관련 자료 찾아줘" → COMPOUND[MEMO_WRITE,KNOWLEDGE_QUERY]

## 핵심 구분법
- 정보를 물어보는 것 = KNOWLEDGE_QUERY
- 새로운 정보를 기록/수정하는 것 = MEMO_WRITE
- 폴더/메모를 정리/이동/삭제하는 것 = MEMO_ORGANIZE
- 목록을 조회하는 것 = MEMO_SEARCH
- 인사/잡담 = GENERAL_CHAT
- 두 가지 이상이 섞인 것 = COMPOUND[의도1,의도2]
- 후속 요청("그래", "해봐", "알려줘", "평가해봐" 등)은 이전 대화의 의도를 이어갑니다

반드시 위 분류 중 하나만 응답하세요. COMPOUND인 경우 COMPOUND[의도1,의도2] 형식으로 응답하세요.

메시지: %s

의도:"""
    }

    /**
     * 메시지의 의도를 분류하고, 복합 의도인 경우 서브 의도 목록도 반환합니다.
     * @return Pair<Intent, List<Intent>> - (메인 의도, 복합 의도일 경우 서브 의도 목록)
     */
    fun classifyWithSubIntents(message: String, conversationHistory: String = ""): Pair<Intent, List<Intent>> {
        val prompt = if (conversationHistory.isNotEmpty()) {
            ROUTER_PROMPT_WITH_HISTORY.format(conversationHistory, message)
        } else {
            ROUTER_PROMPT.format(message)
        }

        val response = chatClient.prompt()
            .user(prompt)
            .call()
            .content() ?: "GENERAL_CHAT"

        return parseIntentResponse(response)
    }

    /**
     * 단일 Intent만 반환하는 기존 호환 메서드
     */
    fun classify(message: String, conversationHistory: String = ""): Intent {
        return classifyWithSubIntents(message, conversationHistory).first
    }

    /**
     * LLM 응답을 파싱하여 Intent와 서브 Intent 목록을 반환합니다.
     */
    internal fun parseIntentResponse(response: String): Pair<Intent, List<Intent>> {
        val trimmed = response.trim()

        // COMPOUND[INTENT1,INTENT2] 패턴 매칭
        val compoundPattern = """COMPOUND\[([^\]]+)]""".toRegex(RegexOption.IGNORE_CASE)
        val compoundMatch = compoundPattern.find(trimmed)
        if (compoundMatch != null) {
            val subIntentStr = compoundMatch.groupValues[1]
            val subIntents = subIntentStr.split(",").mapNotNull { parseSimpleIntent(it.trim()) }
            return if (subIntents.size >= 2) {
                Pair(Intent.COMPOUND, subIntents)
            } else if (subIntents.size == 1) {
                Pair(subIntents.first(), emptyList())
            } else {
                Pair(Intent.GENERAL_CHAT, emptyList())
            }
        }

        // 단순 Intent 매칭
        val intent = parseSimpleIntent(trimmed) ?: Intent.GENERAL_CHAT
        return Pair(intent, emptyList())
    }

    private fun parseSimpleIntent(response: String): Intent? {
        return when {
            response.contains("MEMO_WRITE", ignoreCase = true) -> Intent.MEMO_WRITE
            response.contains("MEMO_ORGANIZE", ignoreCase = true) -> Intent.MEMO_ORGANIZE
            response.contains("MEMO_SEARCH", ignoreCase = true) -> Intent.MEMO_SEARCH
            response.contains("MEMO_MANAGEMENT", ignoreCase = true) -> Intent.MEMO_WRITE // 하위 호환
            response.contains("MEMO_CREATION", ignoreCase = true) -> Intent.MEMO_WRITE // 하위 호환
            response.contains("KNOWLEDGE_QUERY", ignoreCase = true) -> Intent.KNOWLEDGE_QUERY
            response.contains("QUESTION", ignoreCase = true) -> Intent.KNOWLEDGE_QUERY // 하위 호환
            response.contains("GENERAL_CHAT", ignoreCase = true) -> Intent.GENERAL_CHAT
            else -> null
        }
    }
}
