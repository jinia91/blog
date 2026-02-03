package kr.co.jiniaslog.ai.usecase

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.ai.domain.chat.ChatSession
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ICreateChatSessionUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var createChatSession: ICreateChatSession

    @Autowired
    private lateinit var getChatSessions: IGetChatSessions

    @Nested
    inner class `채팅 세션 생성 테스트` {
        @Test
        fun `기본 타이틀로 세션을 생성할 수 있다`() {
            // given
            val authorId = 1L

            // when
            val result = createChatSession(
                ICreateChatSession.Command(authorId = authorId)
            )

            // then
            result.sessionId shouldNotBe null
            result.title shouldBe ChatSession.DEFAULT_TITLE
        }

        @Test
        fun `커스텀 타이틀로 세션을 생성할 수 있다`() {
            // given
            val authorId = 1L
            val customTitle = "나의 첫 번째 AI 대화"

            // when
            val result = createChatSession(
                ICreateChatSession.Command(
                    authorId = authorId,
                    title = customTitle,
                )
            )

            // then
            result.title shouldBe customTitle
        }

        @Test
        fun `생성된 세션이 목록에 나타난다`() {
            // given
            val authorId = 2L
            createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "세션 1")
            )
            createChatSession(
                ICreateChatSession.Command(authorId = authorId, title = "세션 2")
            )

            // when
            val sessions = getChatSessions(IGetChatSessions.Query(authorId = authorId))

            // then
            sessions.size shouldBe 2
        }
    }
}
