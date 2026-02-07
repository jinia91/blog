package kr.co.jiniaslog.ai.service

import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory chat history management for Spring AI 1.1.2
 *
 * Spring AI 1.1.2의 제한된 ChatMemory API를 대체하기 위한 커스텀 구현
 * - conversationId(sessionId)별로 메시지 히스토리를 메모리에 저장
 * - 최근 N개 메시지만 유지하여 컨텍스트 윈도우 관리
 */
@Service
class ChatMemoryService {

    private val conversations = ConcurrentHashMap<String, MutableList<Message>>()

    companion object {
        private const val DEFAULT_MAX_MESSAGES = 20 // 최근 20개 메시지만 유지
    }

    /**
     * Add user message to conversation history
     */
    fun addUserMessage(conversationId: String, content: String) {
        val messages = conversations.getOrPut(conversationId) { mutableListOf() }
        messages.add(UserMessage(content))
        trimMessages(conversationId)
    }

    /**
     * Add assistant message to conversation history
     */
    fun addAssistantMessage(conversationId: String, content: String) {
        val messages = conversations.getOrPut(conversationId) { mutableListOf() }
        messages.add(AssistantMessage(content))
        trimMessages(conversationId)
    }

    /**
     * Get conversation history for a given conversationId
     */
    fun getMessages(conversationId: String): List<Message> {
        return conversations[conversationId]?.toList() ?: emptyList()
    }

    /**
     * Clear conversation history for a given conversationId
     */
    fun clear(conversationId: String) {
        conversations.remove(conversationId)
    }

    /**
     * Clear all conversation histories
     */
    fun clearAll() {
        conversations.clear()
    }

    /**
     * Trim messages to keep only the most recent ones within max limit
     */
    private fun trimMessages(conversationId: String, maxMessages: Int = DEFAULT_MAX_MESSAGES) {
        val messages = conversations[conversationId] ?: return
        if (messages.size > maxMessages) {
            val toRemove = messages.size - maxMessages
            repeat(toRemove) {
                messages.removeAt(0)
            }
        }
    }
}
