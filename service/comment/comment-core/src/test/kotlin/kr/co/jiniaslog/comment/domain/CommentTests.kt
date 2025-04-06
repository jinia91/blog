package kr.co.jiniaslog.comment.domain

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.shared.SimpleUnitTestContext
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
            val comment = CommentTestFixtures.createNoneUserComment(
                userName = userName,
                userPassword = userPassword
            )

            // then
            comment.userInfo.userId.shouldBeNull()
            comment.userInfo.userName shouldBe userName
            comment.userInfo.password shouldBe userPassword
            comment.refId.value shouldBe comment.refId.value
        }

        @Test
        fun `유저가 있으면 댓글을 생성할 수 있다`() {
            // given
            val userId = 1L
            val userName = "userName"
            val userPassword = "userPassword"

            // when
            val comment = CommentTestFixtures.createUserComment(
                userId = userId,
                userName = userName,
                userPassword = userPassword
            )

            // then
            comment.userInfo.userId shouldBe userId
            comment.userInfo.userName shouldBe userName
            comment.userInfo.password shouldBe userPassword
        }
    }
}
