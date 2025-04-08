package kr.co.jiniaslog.comment.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.CommentTestFixtures
import kr.co.jiniaslog.comment.domain.ReferenceId
import kr.co.jiniaslog.comment.outbound.CommentRepository
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ICreateCommentRequestTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: ICreateComment

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var commentRepository: CommentRepository

    @Test
    fun `등록된 유저는 아티클 타입에 루트 댓글을 만들 수 있다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
        val user = userRepository.save(User.newOne(NickName("testUser"), Email("test@Tst.com"), null))

        // when
        val result = sut.handle(
            ICreateComment.Command(
                refId = article.entityId.value.let { ReferenceId(it) },
                refType = Comment.RefType.ARTICLE,
                userId = user.entityId.value,
                userName = null,
                password = null,
                parentId = null,
                content = "test comment"
            ),
        )

        // then
        result.shouldNotBeNull()
        val comment = commentRepository.findById(result.commentId)
        comment.shouldNotBeNull()
        comment.refId.value shouldBe article.entityId.value
    }

    @Test
    fun `등록되지 않은 임시유저는 아티클 타입에 루트 댓글을 만들 수 있다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

        // when
        val result = sut.handle(
            ICreateComment.Command(
                refId = article.entityId.value.let { ReferenceId(it) },
                refType = Comment.RefType.ARTICLE,
                userId = null,
                userName = "testUser",
                password = "testPassword",
                parentId = null,
                content = "test comment"
            ),
        )

        // then
        result.shouldNotBeNull()
        val comment = commentRepository.findById(result.commentId)
        comment.shouldNotBeNull()
        comment.refId.value shouldBe article.entityId.value
    }

    @Test
    fun `참조하려는 대상이 없으면 에러가 발생한다`() {
        // when then
        shouldThrow<IllegalArgumentException> {
            sut.handle(
                ICreateComment.Command(
                    refId = ReferenceId(-1L),
                    refType = Comment.RefType.ARTICLE,
                    userId = null,
                    userName = "testUser",
                    password = "testPassword",
                    parentId = null,
                    content = "test comment"
                ),
            )
        }
    }

    @Test
    fun `등록된 유저가 존재하지 않으면 에러가 발생한다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

        // when then
        shouldThrow<IllegalArgumentException> {
            sut.handle(
                ICreateComment.Command(
                    refId = article.entityId.value.let { ReferenceId(it) },
                    refType = Comment.RefType.ARTICLE,
                    userId = -1L,
                    userName = null,
                    password = null,
                    parentId = null,
                    content = "test comment"
                ),
            )
        }
    }

    @Test
    fun `부모 댓글 아이디가 있는데 부모 댓글이 존재하지 않으면 에러가 발생한다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

        // when then
        shouldThrow<IllegalArgumentException> {
            sut.handle(
                ICreateComment.Command(
                    refId = article.entityId.value.let { ReferenceId(it) },
                    refType = Comment.RefType.ARTICLE,
                    userId = null,
                    userName = "testUser",
                    password = "testPassword",
                    parentId = CommentId(214L),
                    content = "test comment"
                ),
            )
        }
    }

    @Test
    fun `부모 댓글이 존재하면 하위에 댓글이 생성된다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
        val parentComment = commentRepository.save(CommentTestFixtures.createAnonymousComment())

        // when
        val result = sut.handle(
            ICreateComment.Command(
                refId = article.entityId.value.let { ReferenceId(it) },
                refType = Comment.RefType.ARTICLE,
                userId = null,
                userName = "testUser",
                password = "testPassword",
                parentId = parentComment.id,
                content = "test comment"
            ),
        )

        // then
        result.shouldNotBeNull()
        val comment = commentRepository.findById(result.commentId)
        comment.shouldNotBeNull()
        comment.refId.value shouldBe article.entityId.value
    }
}
