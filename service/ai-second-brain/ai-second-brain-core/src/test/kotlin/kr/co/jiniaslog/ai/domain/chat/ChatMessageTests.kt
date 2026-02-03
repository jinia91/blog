package kr.co.jiniaslog.ai.domain.chat

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ChatMessageTests : SimpleUnitTestContext() {

    @Nested
    inner class `ChatMessage 생성 테스트` {
        @Test
        fun `유효한 데이터로 USER 메시지를 생성할 수 있다`() {
            // given
            val sessionId = ChatSessionId(1L)
            val content = "안녕하세요"

            // when
            val message = ChatMessage.create(
                sessionId = sessionId,
                role = MessageRole.USER,
                content = content,
            )

            // then
            message.entityId shouldNotBe null
            message.sessionId shouldBe sessionId
            message.role shouldBe MessageRole.USER
            message.content shouldBe content
        }

        @Test
        fun `ASSISTANT 메시지를 생성할 수 있다`() {
            // given
            val sessionId = ChatSessionId(1L)
            val content = "안녕하세요! 무엇을 도와드릴까요?"

            // when
            val message = ChatMessage.create(
                sessionId = sessionId,
                role = MessageRole.ASSISTANT,
                content = content,
            )

            // then
            message.role shouldBe MessageRole.ASSISTANT
        }

        @Test
        fun `빈 내용으로 메시지를 생성하면 예외가 발생한다`() {
            // given
            val sessionId = ChatSessionId(1L)

            // when & then
            shouldThrow<IllegalArgumentException> {
                ChatMessage.create(
                    sessionId = sessionId,
                    role = MessageRole.USER,
                    content = "",
                )
            }
        }

        @Test
        fun `공백만 있는 내용으로 메시지를 생성하면 예외가 발생한다`() {
            // given
            val sessionId = ChatSessionId(1L)

            // when & then
            shouldThrow<IllegalArgumentException> {
                ChatMessage.create(
                    sessionId = sessionId,
                    role = MessageRole.USER,
                    content = "   ",
                )
            }
        }
    }
}
