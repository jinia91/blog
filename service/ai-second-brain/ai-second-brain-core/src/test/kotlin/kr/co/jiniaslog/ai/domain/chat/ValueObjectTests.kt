package kr.co.jiniaslog.ai.domain.chat

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValueObjectTests : SimpleUnitTestContext() {

    @Nested
    inner class `ChatSessionId 테스트` {
        @Test
        fun `양수 값으로 생성할 수 있다`() {
            val id = ChatSessionId(1L)
            id.value shouldBe 1L
        }

        @Test
        fun `0으로 생성하면 예외가 발생한다`() {
            shouldThrow<IllegalArgumentException> {
                ChatSessionId(0L)
            }
        }

        @Test
        fun `음수로 생성하면 예외가 발생한다`() {
            shouldThrow<IllegalArgumentException> {
                ChatSessionId(-1L)
            }
        }
    }

    @Nested
    inner class `ChatMessageId 테스트` {
        @Test
        fun `양수 값으로 생성할 수 있다`() {
            val id = ChatMessageId(1L)
            id.value shouldBe 1L
        }

        @Test
        fun `0으로 생성하면 예외가 발생한다`() {
            shouldThrow<IllegalArgumentException> {
                ChatMessageId(0L)
            }
        }
    }

    @Nested
    inner class `AuthorId 테스트` {
        @Test
        fun `양수 값으로 생성할 수 있다`() {
            val id = AuthorId(1L)
            id.value shouldBe 1L
        }

        @Test
        fun `0으로 생성하면 예외가 발생한다`() {
            shouldThrow<IllegalArgumentException> {
                AuthorId(0L)
            }
        }
    }

    @Nested
    inner class `MessageRole 테스트` {
        @Test
        fun `모든 enum 값이 존재한다`() {
            val values = MessageRole.values()
            values.size shouldBe 3
            values.toSet() shouldBe setOf(MessageRole.USER, MessageRole.ASSISTANT, MessageRole.SYSTEM)
        }

        @Test
        fun `valueOf로 USER를 가져올 수 있다`() {
            MessageRole.valueOf("USER") shouldBe MessageRole.USER
        }

        @Test
        fun `valueOf로 ASSISTANT를 가져올 수 있다`() {
            MessageRole.valueOf("ASSISTANT") shouldBe MessageRole.ASSISTANT
        }

        @Test
        fun `valueOf로 SYSTEM을 가져올 수 있다`() {
            MessageRole.valueOf("SYSTEM") shouldBe MessageRole.SYSTEM
        }

        @Test
        fun `values()는 모든 예상 값을 반환한다`() {
            val values = MessageRole.values()
            values shouldBe arrayOf(MessageRole.USER, MessageRole.ASSISTANT, MessageRole.SYSTEM)
        }
    }
}
