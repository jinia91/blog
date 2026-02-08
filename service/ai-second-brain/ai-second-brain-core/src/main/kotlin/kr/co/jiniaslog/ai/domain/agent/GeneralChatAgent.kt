package kr.co.jiniaslog.ai.domain.agent

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * General Chat Agent - 일반 대화 처리
 *
 * RAG 없이 대화 히스토리만 유지하는 경량 에이전트
 * GENERAL_CHAT 인텐트에서 사용됩니다.
 */
@Component
class GeneralChatAgent(
    @Qualifier("generalChatClient") private val chatClient: ChatClient
) {
    companion object {
        private const val SYSTEM_PROMPT_TEMPLATE = """당신은 사용자의 개인 지식 관리 시스템 AI 어시스턴트입니다.
사용자와 자연스럽게 대화하며 도움을 제공합니다.
이전 대화 맥락을 참고하여 일관된 대화를 이어갑니다.
답변은 한국어로, 명확하고 간결하게 제공합니다.

## 현재 시간 정보
%s"""
    }

    internal fun buildSystemPrompt(): String {
        val now = LocalDateTime.now()
        val timeInfo = """현재: ${now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E) HH:mm"))}
오늘: ${now.format(DateTimeFormatter.ofPattern("MM월 dd일 (E)"))}
내일: ${now.plusDays(1).format(DateTimeFormatter.ofPattern("MM월 dd일 (E)"))}
모레: ${now.plusDays(2).format(DateTimeFormatter.ofPattern("MM월 dd일 (E)"))}"""

        return SYSTEM_PROMPT_TEMPLATE.format(timeInfo)
    }

    fun chat(
        message: String,
        sessionId: Long
    ): String {
        return chatClient.prompt()
            .system(buildSystemPrompt())
            .user(message)
            .advisors { advisorSpec ->
                advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId.toString())
            }
            .call()
            .content() ?: "응답을 생성할 수 없습니다."
    }
}
