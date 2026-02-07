package kr.co.jiniaslog.ai.domain.agent

import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Intent Router Agent - 경량 모델을 사용하여 사용자 메시지의 의도를 분류합니다.
 * 토큰 최적화를 위해 최소한의 프롬프트와 단순한 응답만 사용합니다.
 */
@Component
class IntentRouterAgent(
    @Qualifier("lightweightChatClient") private val chatClient: ChatClient
) {
    companion object {
        private const val ROUTER_PROMPT = """당신은 사용자 메시지의 의도를 분류하는 라우터입니다.
다음 중 하나로만 응답하세요:

- QUESTION: 정보를 묻는 질문 (예: "~이 뭐야?", "~에 대해 알려줘", "~는 어떻게 해?", "다음주에 뭐해?")
- MEMO_MANAGEMENT: 메모/폴더 관리 요청
  - 생성: "~를 메모해줘", "~를 기록해", "노트로 저장해", "폴더 만들어줘"
  - 수정: "메모 수정해줘", "제목 바꿔줘", "폴더 이름 변경"
  - 삭제: "메모 삭제해", "폴더 지워줘"
  - 이동: "메모를 폴더로 옮겨줘", "폴더 이동해줘"
  - 조회: "메모 목록 보여줘", "폴더 뭐있어?", "내 메모들"
- GENERAL_CHAT: 일반적인 대화, 인사, 감사 표현 등

메시지: %s

의도(QUESTION/MEMO_MANAGEMENT/GENERAL_CHAT만 응답):"""
    }

    fun classify(message: String): Intent {
        val response = chatClient.prompt()
            .user(ROUTER_PROMPT.format(message))
            .call()
            .content() ?: "GENERAL_CHAT"

        return when {
            response.contains("MEMO_MANAGEMENT", ignoreCase = true) -> Intent.MEMO_MANAGEMENT
            response.contains("MEMO_CREATION", ignoreCase = true) -> Intent.MEMO_MANAGEMENT // 하위 호환
            response.contains("QUESTION", ignoreCase = true) -> Intent.QUESTION
            else -> Intent.GENERAL_CHAT
        }
    }
}
