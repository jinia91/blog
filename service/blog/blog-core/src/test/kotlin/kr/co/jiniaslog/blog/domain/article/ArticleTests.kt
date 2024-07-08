package kr.co.jiniaslog.blog.domain.article

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ArticleTests : SimpleUnitTestContext() {

    @Nested
    inner class `게시글 불변성 테스트`() {
        @Test
        fun `유효한 유저가 게시글을 시작하면 게시글이 생성된다`() {
            // given
            val authorId = UserId(IdUtils.generate())

            // when
            val newArticle = Article.newOne(authorId = authorId)

            // then
            newArticle.status shouldBe Article.Status.DRAFT
            newArticle.categoryId.shouldBeNull()
            newArticle.memoRefId.shouldBeNull()
            newArticle.articleContents shouldBe ArticleContents.EMPTY
        }

        @Test
        fun `게시글의 조회수는 음수가 될 수 없다`() {
            // given
            // when, then
            shouldThrow<IllegalArgumentException> {
                ArticleTestFixtures.createPublishedArticle(
                    hit = -1
                )
            }
        }

        @Test
        fun `게시글이 게시된경우 카테고리가 없을 수 없다`() {
            // given
            // when, then
            shouldThrow<IllegalArgumentException> {
                ArticleTestFixtures.createPublishedArticle(
                    categoryId = null
                )
            }
        }

        @Test
        fun `게시글이 게시된경우 제목이 비어있을 수 없다`() {
            // given
            // when, then
            shouldThrow<IllegalArgumentException> {
                ArticleTestFixtures.createPublishedArticle(
                    title = ""
                )
            }
        }

        @Test
        fun `게시글이 게시된경우 내용이 비어있을 수 없다`() {
            // given
            // when, then
            shouldThrow<IllegalArgumentException> {
                ArticleTestFixtures.createPublishedArticle(
                    contents = ""
                )
            }
        }

        @Test
        fun `게시글이 게시된경우 썸네일이 비어있을수 없다`() {
            // given
            // when, then
            shouldThrow<IllegalArgumentException> {
                ArticleTestFixtures.createPublishedArticle(
                    thumbnailUrl = ""
                )
            }
        }

        @Test
        fun `게시글이 삭제될경우 카테고리가 있을수 없다`() {
            // given
            // when, then
            shouldThrow<IllegalArgumentException> {
                ArticleTestFixtures.createPublishedArticle(
                    categoryId = CategoryId(IdUtils.generate()),
                    status = Article.Status.DELETED
                )
            }
        }

        @Test
        fun `게시글이 삭제될경우 태그가 있을수 없다`() {
            // given
            // when, then
            shouldThrow<IllegalArgumentException> {
                ArticleTestFixtures.createPublishedArticle(
                    categoryId = null,
                    tags = listOf(TagId(IdUtils.generate())),
                    status = Article.Status.DELETED
                )
            }
        }
    }

    @Nested
    inner class `게시글 수정 테스트`() {
        @Test
        fun `유효한 컨텐츠로 게시글 수정 요청을 하면 게시글의 내용을 수정할 수 있다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle()
            val newContents = ArticleContents(
                title = "new title",
                contents = "new contents",
                thumbnailUrl = "new thumbnailUrl",
            )

            // when
            article.editContents(newContents)

            // then
            article.articleContents shouldBe newContents
        }
    }

    @Nested
    inner class `게시글 공개 테스트`() {
        @Test
        fun `게시글이 공개 상태로 변경가능한 상태에서 공개를 하면 공개에 성공한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                status = Article.Status.DRAFT
            )
            article.canBePublished() shouldBe true

            // when
            article.publish()

            // then
            article.status shouldBe Article.Status.PUBLISHED
        }

        @Test
        fun `게시된 게시글을 다시 공개하려고 하면 실패한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                status = Article.Status.PUBLISHED
            )
            article.canBePublished() shouldBe false

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.publish()
            }
        }

        @Test
        fun `카테고리가 없는 게시글 초안을 공개하려고 하면 실패한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                categoryId = null,
                status = Article.Status.DRAFT
            )
            article.canBePublished() shouldBe false

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.publish()
            }
        }

        @Test
        fun `제목이 없는 게시글 초안을 공개하려고 하면 실패한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                title = "",
                status = Article.Status.DRAFT

            )
            article.canBePublished() shouldBe false

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.publish()
            }
        }

        @Test
        fun `내용이 없는 게시글 초안을 공개하려고 하면 실패한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                contents = "",
                status = Article.Status.DRAFT

            )
            article.canBePublished() shouldBe false

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.publish()
            }
        }

        @Test
        fun `썸네일이 없는 게시글 초안을 공개하려고 하면 실패한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                thumbnailUrl = "",
                status = Article.Status.DRAFT
            )
            article.canBePublished() shouldBe false

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.publish()
            }
        }
    }
}
