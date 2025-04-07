package kr.co.jiniaslog.comment.queries

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.comment.adapter.outbound.mysql.config.CommentDb
import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentTestFixtures
import kr.co.jiniaslog.comment.domain.ReferenceId
import kr.co.jiniaslog.comment.outbound.CommentRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CommentQueriesTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: IGetHierarchyCommentsByRef

    @Autowired
    lateinit var commentRepository: CommentRepository

    @PersistenceContext(name = CommentDb.ENTITY_MANAGER_FACTORY)
    lateinit var commentEntityManager: EntityManager

    @Test
    fun `부모 댓글과 자식댓글이 존재하면 게시글 참조타입에 따라 한번에 계층형으로 조회할 수 있다`() {
        // given
        withClue("부모 3, 각 부모마다 자식 3") {
            val parent1 = commentRepository.save(CommentTestFixtures.createNoneUserComment(refId = ReferenceId(5L)))
            val parent2 = commentRepository.save(CommentTestFixtures.createNoneUserComment(refId = ReferenceId(5L)))
            val parent3 = commentRepository.save(CommentTestFixtures.createNoneUserComment(refId = ReferenceId(5L)))
            val child1 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent1.entityId,
                    refId = ReferenceId(5L)
                )
            )
            val child2 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent1.entityId,
                    refId = ReferenceId(5L)
                )
            )
            val child3 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent1.entityId,
                    refId = ReferenceId(5L)
                )
            )
            val child4 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent2.entityId,
                    refId = ReferenceId(5L)
                )
            )
            val child5 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent2.entityId,
                    refId = ReferenceId(5L)
                )
            )
            val child6 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent2.entityId,
                    refId = ReferenceId(5L)
                )
            )
            val child7 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent3.entityId,
                    refId = ReferenceId(5L)
                )
            )
            val child8 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent3.entityId,
                    refId = ReferenceId(5L)
                )
            )
            val child9 = commentRepository.save(
                CommentTestFixtures.createNoneUserComment(
                    parentId = parent3.entityId,
                    refId = ReferenceId(5L)
                )
            )
        }

        // when
        val command = IGetHierarchyCommentsByRef.Query(
            refId = ReferenceId(5L),
            refType = Comment.RefType.ARTICLE
        )
        val result = sut.handle(command)

        commentEntityManager.clear()

        // then
        withClue("부모 3, 각 부모마다 자식 3") {
            result.comments.size shouldBe 3
            result.comments[0].children.size shouldBe 3
            result.comments[1].children.size shouldBe 3
            result.comments[2].children.size shouldBe 3
        }
    }

    @Test
    fun `4단 계층으로도 댓글작성이 가능하다`() {
        // given
        val parent1 = commentRepository.save(CommentTestFixtures.createNoneUserComment(refId = ReferenceId(5L)))
        val child1 = commentRepository.save(
            CommentTestFixtures.createNoneUserComment(
                parentId = parent1.entityId,
                refId = ReferenceId(5L)
            )
        )
        val child2 = commentRepository.save(
            CommentTestFixtures.createNoneUserComment(
                parentId = child1.entityId,
                refId = ReferenceId(5L)
            )
        )
        val child3 = commentRepository.save(
            CommentTestFixtures.createNoneUserComment(
                parentId = child2.entityId,
                refId = ReferenceId(5L)
            )
        )

        // when
        val command = IGetHierarchyCommentsByRef.Query(
            refId = ReferenceId(5L),
            refType = Comment.RefType.ARTICLE
        )
        val result = sut.handle(command)

        commentEntityManager.clear()

        // then
        withClue("계층형 체크") {
            result.comments.size shouldBe 1
            result.comments[0].children.size shouldBe 1
            result.comments[0].children[0].children.size shouldBe 1
            result.comments[0].children[0].children[0].children.size shouldBe 1
            result.comments[0].children[0].children[0].children[0].children.size shouldBe 0
        }
    }
}
