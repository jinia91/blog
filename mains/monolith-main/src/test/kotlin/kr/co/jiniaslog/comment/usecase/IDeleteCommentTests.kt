package kr.co.jiniaslog.comment.usecase

import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentTestFixtures
import kr.co.jiniaslog.comment.outbound.CommentRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IDeleteCommentTests : TestContainerAbstractSkeleton() {
    @Autowired
    private lateinit var sut: IDeleteComment

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Test
    fun `익명 유저는 패스워드가 일치하면 자신의 댓글을 삭제할 수 있다`() {
        // given
        val password = "password"

        val asIsComment = withClue("익명 유저 기존 댓글이 존재한다") {
            commentRepository.save(
                CommentTestFixtures.createAnonymousComment(
                    userPassword = password
                )
            )
        }

        val command = IDeleteComment.Command(
            asIsComment.entityId,
            null,
            password
        )

        // when
        val result = sut.handle(command)

        // then
        val foundComment = commentRepository.findById(asIsComment.entityId)
        foundComment.shouldNotBeNull()
        foundComment.status shouldBe Comment.Status.DELETED
    }
}
