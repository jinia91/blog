package kr.co.jiniaslog.ai.domain.chat

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ChatSessionTests : SimpleUnitTestContext() {

    @Nested
    inner class `ChatSession 생성 테스트` {
        @Test
        fun `유효한 authorId로 세션을 생성할 수 있다`() {
            // given
            val authorId = AuthorId(1L)

            // when
            val session = ChatSession.create(authorId = authorId)

            // then
            session.entityId shouldNotBe null
            session.authorId shouldBe authorId
            session.title shouldBe ChatSession.DEFAULT_TITLE
        }

        @Test
        fun `커스텀 타이틀로 세션을 생성할 수 있다`() {
            // given
            val authorId = AuthorId(1L)
            val customTitle = "나의 첫 번째 대화"

            // when
            val session = ChatSession.create(authorId = authorId, title = customTitle)

            // then
            session.title shouldBe customTitle
        }
    }

    @Nested
    inner class `ChatSession 타이틀 수정 테스트` {
        @Test
        fun `세션 타이틀을 수정할 수 있다`() {
            // given
            val session = ChatSession.create(authorId = AuthorId(1L))
            val newTitle = "수정된 타이틀"

            // when
            session.updateTitle(newTitle)

            // then
            session.title shouldBe newTitle
        }

        @Test
        fun `빈 타이틀로 수정하면 예외가 발생한다`() {
            // given
            val session = ChatSession.create(authorId = AuthorId(1L))

            // when & then
            shouldThrow<IllegalArgumentException> {
                session.updateTitle("")
            }
        }

        @Test
        fun `공백만 있는 타이틀로 수정하면 예외가 발생한다`() {
            // given
            val session = ChatSession.create(authorId = AuthorId(1L))

            // when & then
            shouldThrow<IllegalArgumentException> {
                session.updateTitle("   ")
            }
        }
    }
}
