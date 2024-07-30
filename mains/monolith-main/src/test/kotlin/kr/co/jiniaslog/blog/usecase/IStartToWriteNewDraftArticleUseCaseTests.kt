package kr.co.jiniaslog.blog.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.UserId
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import kr.co.jiniaslog.blog.usecase.article.IStartToWriteNewDraftArticle
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IStartToWriteNewDraftArticleUseCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    private lateinit var sut: IStartToWriteNewDraftArticle

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var transactionHandler: BlogTransactionHandler

    @Test
    fun `유효한 유저로 게시물 작성을 시작하면 성공한다`() {
        // given
        val user =
            userRepository.save(
                User.newOne(
                    NickName("jinia"),
                    Email("jinia@google.com"),
                    null
                ),
            )

        val command =
            IStartToWriteNewDraftArticle.Command(
                authorId = UserId(user.entityId.value),
            )

        // when
        val info = sut.handle(command)

        // then
        info shouldNotBe null
        info.articleId shouldNotBe null

        // 상태 검증
        // 영속성 컨텍스트 초기화
        em.clear()
        // 지연로딩 해소를 위한 트랜잭션
        transactionHandler.runInRepeatableReadTransaction {
            val article = articleRepository.findById(info.articleId)
            article.shouldNotBeNull()
            article.memoRefId.shouldBeNull()
            article.categoryId.shouldBeNull()
            article.status shouldBe Article.Status.DRAFT
            article.authorId shouldBe UserId(user.entityId.value)
            article.articleContents shouldBe ArticleContents.EMPTY
            article.tags.size shouldBe 0
            article.hit shouldBe 0
        }
    }

    @Test
    fun `존재하지 않는 유저가 게시물 작성을 시작하면 실패한다`() {
        // given
        val command =
            IStartToWriteNewDraftArticle.Command(
                authorId = UserId(1),
            )

        // when, then
        shouldThrow<IllegalArgumentException> { sut.handle(command) }
    }
}
