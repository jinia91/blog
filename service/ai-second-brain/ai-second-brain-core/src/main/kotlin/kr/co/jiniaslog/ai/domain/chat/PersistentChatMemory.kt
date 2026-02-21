package kr.co.jiniaslog.ai.domain.chat

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage

/**
 * DB 기반 ChatMemory 구현
 *
 * AiUseCasesFacade가 ChatMessage를 DB에 저장하므로,
 * 이 구현체는 DB에서 읽기만 수행합니다.
 * add()는 no-op으로 중복 저장을 방지합니다.
 *
 * 서버 재시작 후에도 대화 히스토리가 유지됩니다.
 */
class PersistentChatMemory(
    private val chatMessageRepository: ChatMessageRepository,
    private val maxMessages: Int = 20,
) : ChatMemory {

    /**
     * No-op: AiUseCasesFacade가 이미 DB에 메시지를 저장합니다.
     * MessageChatMemoryAdvisor가 호출하지만 중복 저장을 방지합니다.
     */
    override fun add(conversationId: String, messages: List<Message>) {
        // No-op - messages are persisted by AiUseCasesFacade
    }

    /**
     * DB에서 대화 히스토리를 로드하여 Spring AI Message로 변환합니다.
     * 최근 maxMessages개만 반환합니다.
     */
    override fun get(conversationId: String): List<Message> {
        val sessionId = try {
            ChatSessionId(conversationId.toLong())
        } catch (e: NumberFormatException) {
            return emptyList()
        }

        val chatMessages = chatMessageRepository.findAllBySessionId(sessionId)

        return chatMessages
            .takeLast(maxMessages)
            .map { chatMessage -> toSpringAiMessage(chatMessage) }
    }

    /**
     * 대화 히스토리를 DB에서 삭제합니다.
     */
    override fun clear(conversationId: String) {
        val sessionId = try {
            ChatSessionId(conversationId.toLong())
        } catch (e: NumberFormatException) {
            return
        }
        chatMessageRepository.deleteAllBySessionId(sessionId)
    }

    private fun toSpringAiMessage(chatMessage: ChatMessage): Message {
        return when (chatMessage.role) {
            MessageRole.USER -> UserMessage(chatMessage.content)
            MessageRole.ASSISTANT -> AssistantMessage(chatMessage.content)
            MessageRole.SYSTEM -> SystemMessage(chatMessage.content)
        }
    }
}
