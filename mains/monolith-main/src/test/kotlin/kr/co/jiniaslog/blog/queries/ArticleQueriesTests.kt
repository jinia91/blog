package kr.co.jiniaslog.blog.queries

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import org.junit.jupiter.api.Nested
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
        val result = sut.handle(IGetSimpleArticles.Query(article1.entityId.value, 3, true))
            .articles

        // then
        result.size shouldBe 2
        result[0].id shouldBe article2.entityId.value
        result[1].id shouldBe article3.entityId.value
    }

    @Nested
    inner class `간소조회 테스트` {
        @Test
        fun `초안 조회를 하면 게시되지 않은 게시물들이 조회된다`() {
            // given
            val article1 = articleRepository.save(ArticleTestFixtures.createDraftArticle())
            val article2 = articleRepository.save(ArticleTestFixtures.createDraftArticle())
            val article3 = articleRepository.save(ArticleTestFixtures.createDraftArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(article1.entityId.value, 3, false))
                .articles

            // then
            result.size shouldBe 2
            result[0].id shouldBe article2.entityId.value
            result[0].title shouldBe article2.draftContents.title
            result[0].content shouldBe article2.draftContents.contents

            result[1].id shouldBe article3.entityId.value
            result[1].title shouldBe article3.draftContents.title
            result[1].content shouldBe article3.draftContents.contents
        }

        @Test
        fun `게시된 게시물을 조회하면 게시된 게시물 내용이 조회된다`() {
            // given
            val article1 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
            val article2 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
            val article3 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(article1.entityId.value, 3, true))
                .articles

            // then
            result.size shouldBe 2
            result[0].id shouldBe article2.entityId.value
            result[0].title shouldBe article2.articleContents.title
            result[0].content shouldBe article2.articleContents.contents

            result[1].id shouldBe article3.entityId.value
            result[1].title shouldBe article3.articleContents.title
            result[1].content shouldBe article3.articleContents.contents
        }

        @Test
        fun `커서가 된 게시된 게시물은 조회되지않는다`() {
            // given
            val article1 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(article1.entityId.value, 3, true))
                .articles

            // then
            result.size shouldBe 0
        }

        @Test
        fun `커서가 된 게시되지 않은 게시물 이후로 조회된다`() {
            // given
            val article1 = articleRepository.save(ArticleTestFixtures.createDraftArticle())
            val article2 = articleRepository.save(ArticleTestFixtures.createDraftArticle())
            val article3 = articleRepository.save(ArticleTestFixtures.createDraftArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(article1.entityId.value, 3, false))
                .articles

            // then
            result.size shouldBe 2
            result[0].id shouldBe article2.entityId.value
            result[1].id shouldBe article3.entityId.value
        }

        @Test
        fun `커서가 된 게시되지 않은 게시물은 조회되지않는다`() {
            // given
            val article1 = articleRepository.save(ArticleTestFixtures.createDraftArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(article1.entityId.value, 3, false))
                .articles

            // then
            result.size shouldBe 0
        }

        @Test
        fun `간소 게시물은 컨텐츠가 100자 이상이면 절삭한다`() {
            // given
            articleRepository.save(
                ArticleTestFixtures.createPublishedArticle(
                    contents = "a".repeat(500)
                )
            )

            // when
            val result = sut.handle(IGetSimpleArticles.Query(1, 3, true))
                .articles

            // then
            result[0].content.length shouldBe 100
        }
    }
}
