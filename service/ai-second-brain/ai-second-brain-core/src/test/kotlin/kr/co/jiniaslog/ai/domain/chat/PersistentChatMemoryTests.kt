package kr.co.jiniaslog.ai.domain.chat

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage

class PersistentChatMemoryTests : SimpleUnitTestContext() {

    private val chatMessageRepository: ChatMessageRepository = mockk()

    private fun makeMessage(id: Long, sessionId: Long, role: MessageRole, content: String): ChatMessage {
        return ChatMessage.from(
            id = ChatMessageId(id),
            sessionId = ChatSessionId(sessionId),
            role = role,
            content = content,
        )
    }

    @Nested
    inner class `get() 테스트` {

        @Test
        fun `get()이 DB에서 메시지를 로드하여 Spring AI Message로 변환한다`() {
            // given
            val sessionId = ChatSessionId(1L)
            val messages = listOf(
                makeMessage(1L, 1L, MessageRole.USER, "안녕하세요"),
                makeMessage(2L, 1L, MessageRole.ASSISTANT, "무엇을 도와드릴까요?"),
                makeMessage(3L, 1L, MessageRole.SYSTEM, "시스템 메시지"),
            )
            every { chatMessageRepository.findAllBySessionId(sessionId) } returns messages

            val sut = PersistentChatMemory(chatMessageRepository, maxMessages = 20)

            // when
            val result = sut.get("1")

            // then
            result.size shouldBe 3
            (result[0] is UserMessage) shouldBe true
            result[0].text shouldBe "안녕하세요"
            (result[1] is AssistantMessage) shouldBe true
            result[1].text shouldBe "무엇을 도와드릴까요?"
            (result[2] is SystemMessage) shouldBe true
            result[2].text shouldBe "시스템 메시지"
        }

        @Test
        fun `get()이 maxMessages 개수만큼만 반환한다`() {
            // given
            val sessionId = ChatSessionId(1L)
            val messages = (1L..25L).map { i ->
                makeMessage(i, 1L, MessageRole.USER, "메시지 $i")
            }
            every { chatMessageRepository.findAllBySessionId(sessionId) } returns messages

            val sut = PersistentChatMemory(chatMessageRepository, maxMessages = 10)

            // when
            val result = sut.get("1")

            // then
            result.size shouldBe 10
            result.first().text shouldBe "메시지 16"
            result.last().text shouldBe "메시지 25"
        }

        @Test
        fun `get()이 빈 히스토리를 정상 처리한다`() {
            // given
            val sessionId = ChatSessionId(1L)
            every { chatMessageRepository.findAllBySessionId(sessionId) } returns emptyList()

            val sut = PersistentChatMemory(chatMessageRepository, maxMessages = 20)

            // when
            val result = sut.get("1")

            // then
            result shouldBe emptyList()
        }

        @Test
        fun `get()이 잘못된 conversationId를 처리한다`() {
            // given
            val sut = PersistentChatMemory(chatMessageRepository, maxMessages = 20)

            // when
            val result = sut.get("not-a-number")

            // then
            result shouldBe emptyList()
        }
    }

    @Nested
    inner class `add() 테스트` {

        @Test
        fun `add()는 아무 동작도 하지 않는다 (no-op)`() {
            // given
            val sut = PersistentChatMemory(chatMessageRepository, maxMessages = 20)
            val springMessages = listOf(UserMessage("테스트"))

            // when
            sut.add("1", springMessages)

            // then - repository 메서드가 호출되지 않아야 함
            verify(exactly = 0) { chatMessageRepository.save(any()) }
            verify(exactly = 0) { chatMessageRepository.saveAll(any()) }
        }
    }

    @Nested
    inner class `clear() 테스트` {

        @Test
        fun `clear()가 deleteAllBySessionId를 호출한다`() {
            // given
            val sessionId = ChatSessionId(1L)
            every { chatMessageRepository.deleteAllBySessionId(sessionId) } returns Unit

            val sut = PersistentChatMemory(chatMessageRepository, maxMessages = 20)

            // when
            sut.clear("1")

            // then
            verify(exactly = 1) { chatMessageRepository.deleteAllBySessionId(sessionId) }
        }

        @Test
        fun `clear()가 잘못된 conversationId를 처리한다`() {
            // given
            val sut = PersistentChatMemory(chatMessageRepository, maxMessages = 20)

            // when
            sut.clear("not-a-number")

            // then - repository 메서드가 호출되지 않아야 함
            verify(exactly = 0) { chatMessageRepository.deleteAllBySessionId(any()) }
        }
    }
}
