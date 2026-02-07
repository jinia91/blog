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

    @Nested
    inner class `ChatMessage from() 팩토리 메서드 테스트` {
        @Test
        fun `모든 유효한 파라미터로 메시지를 생성할 수 있다`() {
            // given
            val id = ChatMessageId(100L)
            val sessionId = ChatSessionId(1L)
            val role = MessageRole.USER
            val content = "테스트 메시지"

            // when
            val message = ChatMessage.from(
                id = id,
                sessionId = sessionId,
                role = role,
                content = content,
            )

            // then
            message.entityId shouldBe id
            message.sessionId shouldBe sessionId
            message.role shouldBe role
            message.content shouldBe content
        }

        @Test
        fun `from()은 주어진 ID를 보존한다 (create()와 달리 새 ID를 생성하지 않음)`() {
            // given
            val predefinedId = ChatMessageId(999L)
            val sessionId = ChatSessionId(1L)

            // when
            val messageFromFactory = ChatMessage.from(
                id = predefinedId,
                sessionId = sessionId,
                role = MessageRole.USER,
                content = "ID 보존 테스트",
            )

            // then
            messageFromFactory.entityId shouldBe predefinedId
        }

        @Test
        fun `USER 역할의 메시지를 생성할 수 있다`() {
            // given
            val id = ChatMessageId(1L)
            val sessionId = ChatSessionId(1L)
            val content = "사용자 메시지"

            // when
            val message = ChatMessage.from(
                id = id,
                sessionId = sessionId,
                role = MessageRole.USER,
                content = content,
            )

            // then
            message.role shouldBe MessageRole.USER
            message.content shouldBe content
        }

        @Test
        fun `ASSISTANT 역할의 메시지를 생성할 수 있다`() {
            // given
            val id = ChatMessageId(2L)
            val sessionId = ChatSessionId(1L)
            val content = "AI 응답 메시지"

            // when
            val message = ChatMessage.from(
                id = id,
                sessionId = sessionId,
                role = MessageRole.ASSISTANT,
                content = content,
            )

            // then
            message.role shouldBe MessageRole.ASSISTANT
            message.content shouldBe content
        }

        @Test
        fun `SYSTEM 역할의 메시지를 생성할 수 있다`() {
            // given
            val id = ChatMessageId(3L)
            val sessionId = ChatSessionId(1L)
            val content = "시스템 프롬프트"

            // when
            val message = ChatMessage.from(
                id = id,
                sessionId = sessionId,
                role = MessageRole.SYSTEM,
                content = content,
            )

            // then
            message.role shouldBe MessageRole.SYSTEM
            message.content shouldBe content
        }
    }
}
