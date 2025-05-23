package kr.co.jiniaslog.blog.queries

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.TagRepository
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.testcontainers.shaded.org.bouncycastle.asn1.x500.style.RFC4519Style.title

class ArticleQueriesTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: ArticleQueriesFacade

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var tagRepository: TagRepository

    @Nested
    inner class `아이디에 따른 게시글의 특정 상태별 데이터 조회` {
        @Test
        fun `초안 게시글은 초안상태 데이터를 조회할 수 있다`() {
            // given
            val article = articleRepository.save(ArticleTestFixtures.createDraftArticle())

            // when
            val result = sut.handle(
                IGetExpectedStatusArticleById.Query(article.entityId, expectedStatus = Article.Status.DRAFT)
            )

            // then
            result.shouldNotBeNull()
            result.id shouldBe article.entityId
            result.title shouldBe article.articleContents.title
        }

        @Test
        fun `초안 게시글은 게시 상태 데이터를 조회할 수 없다`() {
            // given
            val article = articleRepository.save(ArticleTestFixtures.createDraftArticle())

            // when
            shouldThrow<IllegalArgumentException> {
                sut.handle(
                    IGetExpectedStatusArticleById.Query(article.entityId, expectedStatus = Article.Status.PUBLISHED)
                )
            }
        }

        @Test
        fun `삭제된 게시글은 게시상태 데이터를 조회할 수 없다`() {
            // given
            val article = articleRepository.save(ArticleTestFixtures.createDeletedArticle())

            // when
            shouldThrow<IllegalArgumentException> {
                sut.handle(
                    IGetExpectedStatusArticleById.Query(article.entityId, expectedStatus = Article.Status.PUBLISHED)
                )
            }
        }

        @Test
        fun `게시상태 게시글은 초안상태 데이터를 조회할 수 있다`() {
            // given
            val article = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

            // when
            val result = sut.handle(
                IGetExpectedStatusArticleById.Query(article.entityId, expectedStatus = Article.Status.DRAFT)
            )

            // then
            result.shouldNotBeNull()
            result.id shouldBe article.entityId
            result.title shouldBe article.draftContents.title
        }

        @Test
        fun `게시상태 게시글은 게시 상태 데이터를 조회할 수 있다`() {
            // given
            val article = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

            // when
            val result = sut.handle(
                IGetExpectedStatusArticleById.Query(article.entityId, expectedStatus = Article.Status.PUBLISHED)
            )

            // then
            result.shouldNotBeNull()
            result.id shouldBe article.entityId
            result.title shouldBe article.articleContents.title
        }
    }

    @Test
    fun `게시글을 조회할 수 없다`() {
        // when, then
        shouldThrow<IllegalArgumentException> {
            sut.handle(IGetExpectedStatusArticleById.Query(ArticleId(1), expectedStatus = Article.Status.DRAFT))
        }
    }

    @Test
    fun `게시된 게시물들을 커서로 조회할 수 있다 - 1개 이상`() {
        // given
        val article1 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
        val article2 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
        val article3 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

        // when
        val result = sut.handle(IGetSimpleArticles.Query(article3.entityId.value, 3, true))
            .articles

        // then
        result.size shouldBe 2
        result[0].id shouldBe article2.entityId.value
        result[1].id shouldBe article1.entityId.value
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
            val result = sut.handle(IGetSimpleArticles.Query(article3.entityId.value, 3, false))
                .articles

            // then
            result.size shouldBe 2
            result[0].id shouldBe article2.entityId.value
            result[0].title shouldBe article2.draftContents.title
            result[0].content shouldBe article2.draftContents.contents

            result[1].id shouldBe article1.entityId.value
            result[1].title shouldBe article1.draftContents.title
            result[1].content shouldBe article1.draftContents.contents
        }

        @Test
        fun `게시된 게시물을 조회하면 게시된 게시물 내용이 조회된다`() {
            // given
            val article1 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
            val article2 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
            val article3 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(article3.entityId.value, 3, true))
                .articles

            // then
            result.size shouldBe 2
            result[0].id shouldBe article2.entityId.value
            result[0].title shouldBe article2.articleContents.title
            result[0].content shouldBe article2.articleContents.contents

            result[1].id shouldBe article1.entityId.value
            result[1].title shouldBe article1.articleContents.title
            result[1].content shouldBe article1.articleContents.contents
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
        fun `커서가 된 게시되지 않은 게시물 이전이 조회된다`() {
            // given
            val article1 = articleRepository.save(ArticleTestFixtures.createDraftArticle())
            val article2 = articleRepository.save(ArticleTestFixtures.createDraftArticle())
            val article3 = articleRepository.save(ArticleTestFixtures.createDraftArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(article3.entityId.value, 3, false))
                .articles

            // then
            result.size shouldBe 2
            result[0].id shouldBe article2.entityId.value
            result[1].id shouldBe article1.entityId.value
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
        fun `간소 게시물은 컨텐츠가 200자 이상이면 절삭한다`() {
            // given
            articleRepository.save(
                ArticleTestFixtures.createPublishedArticle(
                    contents = "a".repeat(500)
                )
            )

            // when
            val result = sut.handle(IGetSimpleArticles.Query(Long.MAX_VALUE, 3, true))
                .articles

            // then
            result[0].content.length shouldBe 200
        }

        @Test
        fun `태그 조회 쿼리면 해당 태그가 있는 게시물들을 조회한다`() {
            // given
            val tag = tagRepository.save(Tag.newOne(TagName("tag")))
            val article1 = articleRepository.save(
                ArticleTestFixtures.createPublishedArticle(
                    tags = listOf(tag)
                )
            )
            val article2 = articleRepository.save(
                ArticleTestFixtures.createPublishedArticle(
                    tags = listOf(tag)
                )
            )
            val article3 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(null, null, true, null, "tag"))
                .articles

            // then
            result.size shouldBe 2
            result.map { it.id } shouldContain article2.entityId.value
            result.map { it.id } shouldContain article1.entityId.value
        }

        @Test
        fun `단순 게시된 게시물들을 조회한다`() {
            // given
            val article1 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
            val article2 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())
            val article3 = articleRepository.save(ArticleTestFixtures.createPublishedArticle())

            // when
            val result = sut.handle(IGetSimpleArticles.Query(null, null, true, null, null))
                .articles

            // then
            result.size shouldBe 3
            result.map { it.id } shouldContain article3.entityId.value
            result.map { it.id } shouldContain article2.entityId.value
            result.map { it.id } shouldContain article1.entityId.value
        }

        @Test
        fun `지원하지 않는 쿼리면, 조회되지 않는다`() {
            // when, then
            shouldThrow<IllegalArgumentException> {
                sut.handle(IGetSimpleArticles.Query(null, 3, false, null, null))
            }
        }
    }
}
