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
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IPublishArticleUseCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    private lateinit var sut: IPublishArticle

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun `게시가능한 게시글 초안을 게시하려하면 성공한다`() {
        // given
        val command = IPublishArticle.Command(ArticleId(1L))
        val draftArticle = ArticleTestFixtures.createPublishedArticle(
            id = ArticleId(1L),
            status = Article.Status.DRAFT,
        ).also {
            it.canPublish shouldBe true
        }
        articleRepository.save(draftArticle)

        // when
        val result = sut.handle(command)

        // then
        result.articleId shouldBe draftArticle.entityId
        em.clear()

        val publishedArticle = articleRepository.findById(result.articleId)
        publishedArticle.shouldNotBeNull()
        publishedArticle.status shouldBe Article.Status.PUBLISHED
    }

    @Test
    fun `게시글이 없는 게시글을 게시하려하면 예외가 발생한다`() {
        // given
        val command = IPublishArticle.Command(ArticleId(1L))

        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(command)
        }
    }
}
