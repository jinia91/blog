package kr.co.jiniaslog.ai.usecase

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageRepository
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.ai.domain.chat.MessageRole
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IGetChatSessionsUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var getChatSessions: IGetChatSessions

    @Autowired
    private lateinit var createChatSession: ICreateChatSession

    @Autowired
    private lateinit var chatMessageRepository: ChatMessageRepository

    @Nested
    inner class `채팅 세션 목록 조회 테스트` {
        @Test
        fun `사용자의 모든 세션을 조회할 수 있다`() {
            // given
            val authorId = 100L
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 1"))
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 2"))
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 3"))

            // when
            val result = getChatSessions(IGetChatSessions.Query(authorId = authorId))

            // then
            result.sessions.size shouldBe 3
        }

        @Test
        fun `세션이 없는 사용자는 빈 리스트를 받는다`() {
            // given
            val authorId = 999L

            // when
            val result = getChatSessions(IGetChatSessions.Query(authorId = authorId))

            // then
            result.sessions.size shouldBe 0
            result.hasNext shouldBe false
        }

        @Test
        fun `다른 사용자의 세션은 조회되지 않는다`() {
            // given
            val authorId1 = 100L
            val authorId2 = 200L
            createChatSession(ICreateChatSession.Command(authorId = authorId1, title = "사용자1 세션"))
            createChatSession(ICreateChatSession.Command(authorId = authorId2, title = "사용자2 세션"))

            // when
            val result1 = getChatSessions(IGetChatSessions.Query(authorId = authorId1))
            val result2 = getChatSessions(IGetChatSessions.Query(authorId = authorId2))

            // then
            result1.sessions.size shouldBe 1
            result2.sessions.size shouldBe 1
        }

        @Test
        fun `세션 정보에 생성일시가 포함된다`() {
            // given
            val authorId = 100L
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "테스트 세션"))

            // when
            val result = getChatSessions(IGetChatSessions.Query(authorId = authorId))

            // then
            result.sessions[0].createdAt shouldNotBe null
        }

        @Test
        fun `정확히 size+1개의 세션이 있을 때 hasNext는 true이다`() {
            // given
            val authorId = 100L
            val size = 2
            // size+1 = 3개의 세션 생성
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 1"))
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 2"))
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 3"))

            // when
            val result = getChatSessions(IGetChatSessions.Query(authorId = authorId, size = size))

            // then
            result.sessions.size shouldBe size
            result.hasNext shouldBe true
            result.nextCursor shouldNotBe null
        }

        @Test
        fun `lastMessage가 100자를 초과하면 잘린다`() {
            // given
            val authorId = 100L
            val longMessage = "a".repeat(150) // 150자 메시지 생성
            val sessionInfo = createChatSession(ICreateChatSession.Command(authorId = authorId, title = "테스트 세션"))

            // 직접 메시지 생성 및 저장
            val message = ChatMessage.create(
                sessionId = ChatSessionId(sessionInfo.sessionId),
                role = MessageRole.USER,
                content = longMessage
            )
            chatMessageRepository.save(message)

            // when
            val result = getChatSessions(IGetChatSessions.Query(authorId = authorId))

            // then
            result.sessions[0].lastMessage shouldNotBe null
            result.sessions[0].lastMessage?.length shouldBe 100
        }

        @Test
        fun `메시지가 없는 세션의 lastMessage는 null이다`() {
            // given
            val authorId = 100L
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "빈 세션"))

            // when
            val result = getChatSessions(IGetChatSessions.Query(authorId = authorId))

            // then
            result.sessions[0].lastMessage shouldBe null
        }

        @Test
        fun `마지막 페이지에서는 hasNext가 false이다`() {
            // given
            val authorId = 100L
            val size = 5
            // 정확히 size개의 세션만 생성
            repeat(size) { i ->
                createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 $i"))
            }

            // when
            val result = getChatSessions(IGetChatSessions.Query(authorId = authorId, size = size))

            // then
            result.sessions.size shouldBe size
            result.hasNext shouldBe false
            result.nextCursor shouldBe null
        }

        @Test
        fun `커서 기반 페이징이 올바르게 동작한다`() {
            // given
            val authorId = 100L
            val size = 2
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 1"))
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 2"))
            createChatSession(ICreateChatSession.Command(authorId = authorId, title = "세션 3"))

            // when - 첫 페이지
            val firstPage = getChatSessions(IGetChatSessions.Query(authorId = authorId, size = size))

            // then - 첫 페이지
            firstPage.sessions.size shouldBe size
            firstPage.hasNext shouldBe true
            firstPage.nextCursor shouldNotBe null

            // when - 두 번째 페이지
            val secondPage = getChatSessions(
                IGetChatSessions.Query(authorId = authorId, cursor = firstPage.nextCursor, size = size)
            )

            // then - 두 번째 페이지
            secondPage.sessions.size shouldBe 1
            secondPage.hasNext shouldBe false
            secondPage.nextCursor shouldBe null
        }
    }
}
