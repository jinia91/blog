package kr.co.jiniaslog.ai.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.ai.domain.chat.ChatMessageRepository
import kr.co.jiniaslog.ai.domain.chat.ChatSessionRepository
import kr.co.jiniaslog.ai.domain.chat.MessageRole
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IChatUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var chatUseCase: IChat

    @Autowired
    private lateinit var createChatSession: ICreateChatSession

    @Autowired
    private lateinit var chatSessionRepository: ChatSessionRepository

    @Autowired
    private lateinit var chatMessageRepository: ChatMessageRepository

    @Nested
    inner class `채팅 테스트` {
        @Test
        fun `유효한 세션에서 채팅을 하면 응답을 받을 수 있다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "테스트 세션")
            )

            // when
            val result = chatUseCase(
                IChat.Command(
                    sessionId = session.sessionId,
                    authorId = authorId,
                    message = "안녕하세요"
                )
            )

            // then
            result.sessionId shouldBe session.sessionId
            result.response shouldNotBe null
        }

        @Test
        fun `존재하지 않는 세션에서 채팅하면 예외가 발생한다`() {
            // given
            val nonExistentSessionId = 99999L
            val authorId = 100L

            // when & then
            shouldThrow<IllegalArgumentException> {
                chatUseCase(
                    IChat.Command(
                        sessionId = nonExistentSessionId,
                        authorId = authorId,
                        message = "안녕하세요"
                    )
                )
            }
        }

        @Test
        fun `다른 사용자의 세션에서 채팅하면 예외가 발생한다`() {
            // given
            val ownerAuthorId = 100L
            val otherAuthorId = 200L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = ownerAuthorId, title = "테스트 세션")
            )

            // when & then
            shouldThrow<IllegalArgumentException> {
                chatUseCase(
                    IChat.Command(
                        sessionId = session.sessionId,
                        authorId = otherAuthorId,
                        message = "안녕하세요"
                    )
                )
            }
        }

        @Test
        fun `채팅 후 메시지가 저장된다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "테스트 세션")
            )

            // when
            chatUseCase(
                IChat.Command(
                    sessionId = session.sessionId,
                    authorId = authorId,
                    message = "테스트 메시지"
                )
            )

            // then
            val messages = chatMessageRepository.findAllBySessionId(
                kr.co.jiniaslog.ai.domain.chat.ChatSessionId(session.sessionId)
            )
            messages.size shouldBe 2 // USER + ASSISTANT
            messages.any { it.role == MessageRole.USER && it.content == "테스트 메시지" } shouldBe true
            messages.any { it.role == MessageRole.ASSISTANT } shouldBe true
        }
    }
}
