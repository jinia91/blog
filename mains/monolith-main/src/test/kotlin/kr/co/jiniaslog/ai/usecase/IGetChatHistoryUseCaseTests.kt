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
            val result = getChatHistory(
                IGetChatHistory.Query(
                    sessionId = session.sessionId,
                    authorId = authorId
                )
            )

            // then
            result.messages.size shouldBe 2 // USER + ASSISTANT
            result.messages[0].role shouldBe MessageRole.USER
            result.messages[0].content shouldBe "첫 번째 메시지"
            result.messages[1].role shouldBe MessageRole.ASSISTANT
            result.hasNext shouldBe false
        }

        @Test
        fun `빈 세션의 히스토리는 빈 리스트를 반환한다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "빈 세션")
            )

            // when
            val result = getChatHistory(
                IGetChatHistory.Query(
                    sessionId = session.sessionId,
                    authorId = authorId
                )
            )

            // then
            result.messages.size shouldBe 0
            result.hasNext shouldBe false
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
            val result = getChatHistory(
                IGetChatHistory.Query(sessionId = session.sessionId, authorId = authorId)
            )

            // then
            result.messages.size shouldBe 4 // 2 USER + 2 ASSISTANT
            result.messages[0].content shouldBe "메시지 1"
            result.messages[2].content shouldBe "메시지 2"
        }

        @Test
        fun `페이지 크기보다 많은 메시지가 있으면 hasNext가 true이다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "페이징 테스트 세션")
            )

            // 3번 채팅 -> 6개 메시지 (USER + ASSISTANT * 3)
            repeat(3) { i ->
                chatUseCase(
                    IChat.Command(sessionId = session.sessionId, authorId = authorId, message = "메시지 $i")
                )
            }

            // when
            val result = getChatHistory(
                IGetChatHistory.Query(
                    sessionId = session.sessionId,
                    authorId = authorId,
                    size = 2 // 2개씩 가져오기
                )
            )

            // then
            result.messages.size shouldBe 2
            result.hasNext shouldBe true
            result.nextCursor shouldBe result.messages.last().messageId
        }

        @Test
        fun `커서를 이용해 다음 페이지를 조회할 수 있다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "페이징 테스트 세션")
            )

            // 3번 채팅 -> 6개 메시지
            repeat(3) { i ->
                chatUseCase(
                    IChat.Command(sessionId = session.sessionId, authorId = authorId, message = "메시지 $i")
                )
            }

            // 첫 페이지 조회
            val firstPage = getChatHistory(
                IGetChatHistory.Query(
                    sessionId = session.sessionId,
                    authorId = authorId,
                    size = 2
                )
            )

            // when - 커서를 사용해 다음 페이지 조회
            val secondPage = getChatHistory(
                IGetChatHistory.Query(
                    sessionId = session.sessionId,
                    authorId = authorId,
                    cursor = firstPage.nextCursor,
                    size = 2
                )
            )

            // then
            secondPage.messages.size shouldBe 2
            secondPage.hasNext shouldBe true
            // 첫 페이지와 두 번째 페이지의 메시지가 다른지 확인
            val firstPageLastId = firstPage.messages.last().messageId
            val secondPageFirstId = secondPage.messages.first().messageId
            (secondPageFirstId > firstPageLastId) shouldBe true
        }

        @Test
        fun `마지막 페이지 조회 시 hasNext가 false이다`() {
            // given
            val authorId = 100L
            val session = createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "마지막 페이지 테스트")
            )

            // 1번 채팅 -> 2개 메시지
            chatUseCase(
                IChat.Command(sessionId = session.sessionId, authorId = authorId, message = "유일한 메시지")
            )

            // when
            val result = getChatHistory(
                IGetChatHistory.Query(
                    sessionId = session.sessionId,
                    authorId = authorId,
                    size = 10 // 메시지보다 큰 페이지 크기
                )
            )

            // then
            result.messages.size shouldBe 2
            result.hasNext shouldBe false
            result.nextCursor shouldBe null
        }
    }
}
