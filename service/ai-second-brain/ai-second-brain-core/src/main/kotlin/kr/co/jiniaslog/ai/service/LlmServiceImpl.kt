package kr.co.jiniaslog.ai.service

import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.MessageRole
import kr.co.jiniaslog.ai.outbound.ChatContext
import kr.co.jiniaslog.ai.outbound.IntentType
import kr.co.jiniaslog.ai.outbound.LlmService
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Service

@Service
class LlmServiceImpl(
    private val chatClient: ChatClient,
) : LlmService {

    companion object {
        private const val INTENT_CLASSIFICATION_PROMPT = """다음 메시지의 의도를 분류해주세요.
- QUESTION: 질문이나 정보 요청
- MEMO_CREATION: 메모나 노트 작성 요청 (예: "~를 메모해줘", "~를 기록해", "노트 작성")
- GENERAL_CHAT: 일반적인 대화

메시지: %s

의도 (QUESTION/MEMO_CREATION/GENERAL_CHAT만 응답):"""
    }

    override fun chat(userMessage: String, context: ChatContext): String {
        val messages = mutableListOf<Message>()

        messages.add(SystemMessage(context.systemPrompt))

        if (context.relevantDocuments.isNotEmpty()) {
            val docsContext = context.relevantDocuments.joinToString("\n\n") { doc ->
                "### ${doc.title}\n${doc.content}"
            }
            messages.add(
                SystemMessage(
                    """참고할 수 있는 관련 메모들입니다:

$docsContext

위 내용을 참고하여 사용자의 질문에 답변해주세요."""
                )
            )
        }

        context.conversationHistory.takeLast(10).forEach { msg ->
            messages.add(toSpringAiMessage(msg))
        }

        messages.add(UserMessage(userMessage))

        return chatClient.prompt()
            .messages(messages)
            .call()
            .content() ?: "응답을 생성할 수 없습니다."
    }

    override fun classifyIntent(message: String): IntentType {
        val prompt = INTENT_CLASSIFICATION_PROMPT.format(message)

        val response = chatClient.prompt()
            .user(prompt)
            .call()
            .content() ?: "GENERAL_CHAT"

        return when {
            response.contains("MEMO_CREATION", ignoreCase = true) -> IntentType.MEMO_CREATION
            response.contains("QUESTION", ignoreCase = true) -> IntentType.QUESTION
            else -> IntentType.GENERAL_CHAT
        }
    }

    private fun toSpringAiMessage(chatMessage: ChatMessage): Message {
        return when (chatMessage.role) {
            MessageRole.USER -> UserMessage(chatMessage.content)
            MessageRole.ASSISTANT -> AssistantMessage(chatMessage.content)
            MessageRole.SYSTEM -> SystemMessage(chatMessage.content)
        }
    }
}
