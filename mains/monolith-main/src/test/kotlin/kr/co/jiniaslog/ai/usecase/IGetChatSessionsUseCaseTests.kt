package kr.co.jiniaslog.ai.usecase

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IGetChatSessionsUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var getChatSessions: IGetChatSessions

    @Autowired
    private lateinit var createChatSession: ICreateChatSession

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
    }
}
