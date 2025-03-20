package kr.co.jiniaslog.blog.queries

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ArticleQueriesTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: ArticleQueriesFacade

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Test
    fun `초안 게시글을 조회할 수 있다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createDraftArticle())

        // when
        val result = sut.handle(IGetArticleById.Query(article.entityId, isDraft = false))

        // then
        result.shouldNotBeNull()
        result.id shouldBe article.entityId
        result.title shouldBe article.articleContents.title
    }

    @Test
    fun `게시된 게시글을 조회할 수 있다`() {
        // given
        val article = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

        // when
        val result = sut.handle(IGetArticleById.Query(article.entityId, isDraft = true))

        // then
        result.shouldNotBeNull()
        result.id shouldBe article.entityId
        result.title shouldBe article.articleContents.title
    }

    @Test
    fun `게시글을 조회할 수 없다`() {
        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(IGetArticleById.Query(ArticleId(1), isDraft = true))
        }
    }

    @Test
    fun `게시된 게시물들을 커서로 조회할 수 있다 - 1개 이상`() {
        // given
        val article1 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
        val article2 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
        val article3 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

        // when
        val result = sut.handle(IGetPublishedSimpleArticleListWithCursor.Query(article1.entityId, 3, true))

        // then
        result.size shouldBe 2
        result[0].id shouldBe article2.entityId.value
        result[1].id shouldBe article3.entityId.value
    }
}
