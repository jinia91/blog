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
}
