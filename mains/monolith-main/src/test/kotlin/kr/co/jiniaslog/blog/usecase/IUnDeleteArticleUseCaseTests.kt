package kr.co.jiniaslog.blog.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.usecase.article.ArticleStatusChangeFacade
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IUnDeleteArticleUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var sut: ArticleStatusChangeFacade

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var em: EntityManager

    @Test
    fun `삭제된 게시글을 되살리면 되살릴 수 있다`() {
        // given
        val deletedArticle = ArticleTestFixtures.createDeletedArticle()
        articleRepository.save(deletedArticle)

        // when
        val info = sut.handle(IUnDeleteArticle.Command(deletedArticle.entityId))

        //
        info.articleId shouldBe deletedArticle.entityId
        em.clear()
        val foundArticle = articleRepository.findById(deletedArticle.entityId)
        foundArticle.shouldNotBeNull()
        foundArticle.status shouldBe Article.Status.DRAFT
    }

    @Test
    fun `게시글이 이미 삭제되었다면 되살리기 시도시 예외가 발생한다`() {
        // given
        val publishedArticle = ArticleTestFixtures.createPublishedArticle()
        articleRepository.save(publishedArticle)

        // when, then
        shouldThrow<IllegalStateException> {
            sut.handle(IUnDeleteArticle.Command(publishedArticle.entityId))
        }
    }

    @Test
    fun `존재하지 않는 게시글을 되살리려했다면 예외가 발생한다`() {
        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(IUnDeleteArticle.Command(ArticleId(1L)))
        }
    }
}
