package kr.co.jiniaslog.comment.domain

import kr.co.jiniaslog.shared.SimpleUnitTestContext
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CommentTests : SimpleUnitTestContext() {
    @Nested
    inner class `댓글 생성 테스트` {
        @Test
        fun `댓글은 유저아이디가 없어도 생성할 수 있다`() {
            val comment = Comment(
                id = CommentId(IdUtils.idGenerator.generate()),
                userInfo = UserInfo(
                    userId = null,
                    userName = "jiniaslog",
                    password = "password"
                ),
                refId = ReferenceId(IdUtils.idGenerator.generate()),
                status = Comment.Status.ACTIVE,
                contents = CommentContents("댓글 내용")
            )
        }
    }
}
