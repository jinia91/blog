package kr.co.jiniaslog.blog.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.usecase.article.IUnPublishArticle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IUnPublishArticleUseCaseTests : TestContainerAbstractSkeleton() {

    @Autowired
    private lateinit var sut: IUnPublishArticle

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var em: EntityManager

    @Test
    fun `게시된 게시글을 게시 취소하면 성공한다`() {
        // given
        val publishedArticle = ArticleTestFixtures.createPublishedArticle()
        articleRepository.save(publishedArticle)

        // when
        val info = sut.handle(IUnPublishArticle.Command(publishedArticle.entityId))

        // then
        info.articleId shouldBe publishedArticle.entityId
        em.clear()
        val foundArticle = articleRepository.findById(publishedArticle.entityId)
        foundArticle.shouldNotBeNull()
        foundArticle.status shouldBe Article.Status.DRAFT
    }

    @Test
    fun `게시글이 이미 게시 취소되었다면 게시 취소 시도시 예외가 발생한다`() {
        // given
        val draftArticle = ArticleTestFixtures.createDraftArticle()
        articleRepository.save(draftArticle)

        // when, then
        shouldThrow<IllegalStateException> {
            sut.handle(IUnPublishArticle.Command(draftArticle.entityId))
        }
    }
}
