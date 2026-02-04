package kr.co.jiniaslog.ai.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.ai.domain.chat.MessageRole
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IGetChatHistoryUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var getChatHistory: IGetChatHistory

    @Autowired
    private lateinit var createChatSession: ICreateChatSession

    @Autowired
    private lateinit var chatUseCase: IChat

    @Nested
    inner class `채팅 히스토리 조회 테스트` {
        @Test
        fun `유효한 세션의 채팅 히스토리를 조회할 수 있다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "테스트 세션")
            )

            chatUseCase(
                IChat.Command(
                    sessionId = session.sessionId,
                    authorId = authorId,
                    message = "첫 번째 메시지"
                )
            )

            // when
            val history = getChatHistory(
                IGetChatHistory.Query(
                    sessionId = session.sessionId,
                    authorId = authorId
                )
            )

            // then
            history.size shouldBe 2 // USER + ASSISTANT
            history[0].role shouldBe MessageRole.USER
            history[0].content shouldBe "첫 번째 메시지"
            history[1].role shouldBe MessageRole.ASSISTANT
        }

        @Test
        fun `빈 세션의 히스토리는 빈 리스트를 반환한다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "빈 세션")
            )

            // when
            val history = getChatHistory(
                IGetChatHistory.Query(
                    sessionId = session.sessionId,
                    authorId = authorId
                )
            )

            // then
            history.size shouldBe 0
        }

        @Test
        fun `존재하지 않는 세션 조회 시 예외가 발생한다`() {
            // given
            val nonExistentSessionId = 99999L
            val authorId = 100L

            // when & then
            shouldThrow<IllegalArgumentException> {
                getChatHistory(
                    IGetChatHistory.Query(
                        sessionId = nonExistentSessionId,
                        authorId = authorId
                    )
                )
            }
        }

        @Test
        fun `다른 사용자의 세션 히스토리 조회 시 예외가 발생한다`() {
            // given
            val ownerAuthorId = 100L
            val otherAuthorId = 200L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = ownerAuthorId, title = "테스트 세션")
            )

            // when & then
            shouldThrow<IllegalArgumentException> {
                getChatHistory(
                    IGetChatHistory.Query(
                        sessionId = session.sessionId,
                        authorId = otherAuthorId
                    )
                )
            }
        }

        @Test
        fun `여러 번 채팅 후 히스토리가 순서대로 조회된다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "테스트 세션")
            )

            chatUseCase(
                IChat.Command(sessionId = session.sessionId, authorId = authorId, message = "메시지 1")
            )
            chatUseCase(
                IChat.Command(sessionId = session.sessionId, authorId = authorId, message = "메시지 2")
            )

            // when
            val history = getChatHistory(
                IGetChatHistory.Query(sessionId = session.sessionId, authorId = authorId)
            )

            // then
            history.size shouldBe 4 // 2 USER + 2 ASSISTANT
            history[0].content shouldBe "메시지 1"
            history[2].content shouldBe "메시지 2"
        }
    }
}
