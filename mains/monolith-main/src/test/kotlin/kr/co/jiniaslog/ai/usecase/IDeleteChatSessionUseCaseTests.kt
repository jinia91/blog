package kr.co.jiniaslog.ai.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.ai.domain.chat.ChatMessageRepository
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.ai.domain.chat.ChatSessionRepository
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IDeleteChatSessionUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var deleteChatSession: IDeleteChatSession

    @Autowired
    private lateinit var createChatSession: ICreateChatSession

    @Autowired
    private lateinit var chatUseCase: IChat

    @Autowired
    private lateinit var chatSessionRepository: ChatSessionRepository

    @Autowired
    private lateinit var chatMessageRepository: ChatMessageRepository

    @Nested
    inner class `채팅 세션 삭제 테스트` {
        @Test
        fun `유효한 세션을 삭제할 수 있다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "삭제할 세션")
            )

            // when
            deleteChatSession(
                IDeleteChatSession.Command(
                    sessionId = session.sessionId,
                    authorId = authorId
                )
            )

            // then
            val deletedSession = chatSessionRepository.findById(ChatSessionId(session.sessionId))
            deletedSession shouldBe null
        }

        @Test
        fun `세션 삭제 시 관련 메시지도 함께 삭제된다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "메시지가 있는 세션")
            )

            chatUseCase(
                IChat.Command(
                    sessionId = session.sessionId,
                    authorId = authorId,
                    message = "테스트 메시지"
                )
            )

            val sessionId = ChatSessionId(session.sessionId)
            val messagesBeforeDelete = chatMessageRepository.findAllBySessionId(sessionId)
            messagesBeforeDelete.size shouldBe 2 // USER + ASSISTANT

            // when
            deleteChatSession(
                IDeleteChatSession.Command(
                    sessionId = session.sessionId,
                    authorId = authorId
                )
            )

            // then
            val messagesAfterDelete = chatMessageRepository.findAllBySessionId(sessionId)
            messagesAfterDelete.size shouldBe 0
        }

        @Test
        fun `존재하지 않는 세션 삭제 시 예외가 발생한다`() {
            // given
            val nonExistentSessionId = 99999L
            val authorId = 100L

            // when & then
            shouldThrow<IllegalArgumentException> {
                deleteChatSession(
                    IDeleteChatSession.Command(
                        sessionId = nonExistentSessionId,
                        authorId = authorId
                    )
                )
            }
        }

        @Test
        fun `다른 사용자의 세션 삭제 시 예외가 발생한다`() {
            // given
            val ownerAuthorId = 100L
            val otherAuthorId = 200L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = ownerAuthorId, title = "다른 사용자 세션")
            )

            // when & then
            shouldThrow<IllegalArgumentException> {
                deleteChatSession(
                    IDeleteChatSession.Command(
                        sessionId = session.sessionId,
                        authorId = otherAuthorId
                    )
                )
            }
        }
    }
}
