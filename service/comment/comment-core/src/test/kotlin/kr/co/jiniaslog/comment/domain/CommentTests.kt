package kr.co.jiniaslog.comment.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import kr.co.jiniaslog.shared.core.cypher.PasswordHelper
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CommentTests : SimpleUnitTestContext() {
    @Nested
    inner class `댓글 생성 테스트` {
        @Test
        fun `댓글은 유저아이디가 없어도 생성할 수 있다`() {
            // given
            val userName = "userName"
            val userPassword = "userPassword"

            // when
            val comment = CommentTestFixtures.createAnonymousComment(
                userName = userName,
                userPassword = userPassword
            )

            // then
            comment.authorInfo.authorId.shouldBeNull()
            comment.authorInfo.authorName shouldBe userName
            PasswordHelper.matches(userPassword, comment.authorInfo.password!!) shouldBe true
            comment.refId.value shouldBe comment.refId.value
        }

        @Test
        fun `유저가 있으면 댓글을 생성할 수 있다`() {
            // given
            val userId = 1L
            val userName = "userName"

            // when
            val comment = CommentTestFixtures.createUserComment(
                userId = userId,
                userName = userName,
            )

            // then
            comment.authorInfo.authorId shouldBe userId
            comment.authorInfo.authorName shouldBe userName
        }

        @Test
        fun `유저id가 없고 비밀번호도 없으면 예외가 발생한다`() {
            // when, then
            shouldThrow<IllegalArgumentException> {
                AuthorInfo(
                    authorId = null,
                    authorName = "userName",
                    password = null,
                    profileImageUrl = null
                )
            }
        }

        @Test
        fun `참조 아이디는 음수여서는 안된다`() {
            shouldThrow<IllegalArgumentException> {
                ReferenceId(-1L)
            }
        }

        @Test
        fun `댓글 컨텐츠는 1000자 이상일경우 예외가 발생한다`() {
            // given
            val content = "a".repeat(1001)

            // when, then
            shouldThrow<IllegalArgumentException> {
                CommentContents(content)
            }
        }

        @Test
        fun `댓글 컨텐츠는 0자일경우 예외가 발생한다`() {
            // given
            val content = ""

            // when, then
            shouldThrow<IllegalArgumentException> {
                CommentContents(content)
            }
        }
    }
}
