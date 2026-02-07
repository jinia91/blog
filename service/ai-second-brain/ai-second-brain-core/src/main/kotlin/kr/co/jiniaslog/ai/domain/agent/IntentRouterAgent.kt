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
        private const val ROUTER_PROMPT = """사용자 메시지의 의도를 분류하세요.

## 분류 기준

QUESTION (질문):
- 질문 어미: ~냐, ~니, ~야?, ~어?, ~나?, ~까?, ~해?, ~뭐야, ~있어?, ~뭐있어
- 예: "내일 뭐있냐", "회의 언제야?", "약속 뭐있어?", "다음주에 뭐해?"

MEMO_MANAGEMENT (메모 관리):
- 진술 어미: ~있다, ~이다, ~해야함, ~한다, ~했다, ~할거다
- 명령 어미: ~해줘, ~저장해, ~기록해, ~만들어줘, ~삭제해, ~수정해
- 예: "5시에 약속있다", "내일 회의다", "우유 사야함", "메모 삭제해줘"

GENERAL_CHAT (일반 대화):
- 인사, 감사, 잡담
- 예: "안녕", "고마워", "뭐해?"

## 핵심 구분법
- "~냐", "~니", "~야?", "~어?" = 질문 → QUESTION
- "~있다", "~이다", "~한다" = 진술 → MEMO_MANAGEMENT

메시지: %s

의도:"""
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
