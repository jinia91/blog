package kr.co.jiniaslog.blog.domain.article

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.blog.domain.ArticleTestFixtures
import kr.co.jiniaslog.blog.domain.UserId
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagName
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
        fun `게시글이 삭제될경우 태그가 있을수 없다`() {
            // given
            // when, then
            shouldThrow<IllegalStateException> {
                ArticleTestFixtures.createPublishedArticle(
                    tags = listOf(
                        Tag.newOne(TagName("tag"))
                    ),
                    status = Article.Status.DELETED
                )
            }
        }
    }

    @Nested
    inner class `게시글 기본 상태 조회 테스트` {
        @Test
        fun `게시글이 게시된 상태이면 publish 불가능하다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle()

            // when
            val canPublish = article.canPublish

            // then
            canPublish shouldBe false
        }

        @Test
        fun `게시글 publsih상태인지 체크가능하다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle()

            // when
            val isPublished = article.isPublished

            // then
            isPublished shouldBe true
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
            article.updateDraftArticleContents(newContents)

            // then
            article.articleContents shouldBe newContents
        }

        @Test
        fun `게시된 게시글은 제목이 없는 게시글을 수정하려고 하면 실패한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                title = "new title"
            )
            val newContents = ArticleContents(
                title = "",
                contents = "new contents",
                thumbnailUrl = "new thumbnailUrl",
            )

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.updateDraftArticleContents(newContents)
            }
        }

        @Test
        fun `게시된 게시글은 내용이 없는 게시글을 수정하려고 하면 실패한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                contents = "new contents"
            )
            val newContents = ArticleContents(
                title = "new title",
                contents = "",
                thumbnailUrl = "new thumbnailUrl",
            )

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.updateDraftArticleContents(newContents)
            }
        }

        @Test
        fun `게시된 게시글은 썸네일이 없는 게시글을 수정하려고 하면 실패한다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle(
                thumbnailUrl = "new thumbnailUrl"
            )
            val newContents = ArticleContents(
                title = "new title",
                contents = "new contents",
                thumbnailUrl = "",
            )

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.updateDraftArticleContents(newContents)
            }
        }

        @Test
        fun `게시글 초안은 어떤 상태든 수정할 수 있다`() {
            // given
            val article = ArticleTestFixtures.createDraftArticle()
            val newContents = ArticleContents.EMPTY

            // when
            article.updateDraftArticleContents(newContents)

            // then
            article.articleContents shouldBe newContents
        }

        @Test
        fun `삭제된 게시글은 내용 수정할 수 없다`() {
            // given
            val article = ArticleTestFixtures.createDeletedArticle()
            val newContents = ArticleContents(
                title = "new title",
                contents = "new contents",
                thumbnailUrl = "new thumbnailUrl",
            )

            // when, then
            shouldThrow<IllegalStateException> {
                article.updateDraftArticleContents(newContents)
            }
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
            article.canPublish shouldBe true

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
            article.canPublish shouldBe false

            // when, then
            shouldThrow<IllegalStateException> {
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
            article.canPublish shouldBe false

            // when, then
            shouldThrow<IllegalStateException> {
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
            article.canPublish shouldBe false

            // when, then
            shouldThrow<IllegalStateException> {
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
            article.canPublish shouldBe false

            // when, then
            shouldThrow<IllegalStateException> {
                article.publish()
            }
        }
    }

    @Nested
    inner class `게시글 삭제 테스트`() {
        @Test
        fun `게시글이 삭제되면 모든 연관관계가 해제된다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle()

            // when
            article.delete()

            // then
            article.tags.size shouldBe 0
            article.memoRefId.shouldBeNull()
            article.status shouldBe Article.Status.DELETED
        }

        @Test
        fun `삭제된 게시글은 재삭제할 수 없다`() {
            // given
            val article = ArticleTestFixtures.createDeletedArticle()

            // when, then
            shouldThrow<IllegalStateException> {
                article.delete()
            }
        }
    }

    @Nested
    inner class `게시글 되살리기 테스트`() {
        @Test
        fun `삭제된 게시글을 되살리면 게시글 초안이 된다`() {
            // given
            val article = ArticleTestFixtures.createDeletedArticle()

            // when
            article.unDelete()

            // then
            article.status shouldBe Article.Status.DRAFT
        }

        @Test
        fun `삭제된 게시글이 아니면 되살리기를할 수 없다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle()

            // when, then
            shouldThrow<IllegalStateException> {
                article.unDelete()
            }
        }
    }

    @Nested
    inner class `게시글 태그 추가 테스트`() {
        @Test
        fun `기존 게시글에 태그를 추가할 수 있다`() {
            // given
            val article = ArticleTestFixtures.createPublishedArticle()
            val tag = Tag.newOne(TagName("tag"))

            // when
            article.addTag(tag)

            // then
            article.tags.size shouldBe 1
        }

        @Test
        fun `삭제된 게시글은 태그를 추가할 수 없다`() {
            // given
            val article = ArticleTestFixtures.createDeletedArticle()
            val tag = Tag.newOne(TagName("tag"))

            // when, then
            shouldThrow<IllegalStateException> {
                article.addTag(tag)
            }
        }
    }

    @Nested
    inner class `게시글 태그 삭제 테스트`() {
        @Test
        fun `기존 게시글에 태그를 삭제할 수 있다`() {
            // given
            val tag = Tag.newOne(TagName("tag"))
            val article = ArticleTestFixtures.createPublishedArticle(
                tags = listOf(tag)
            )

            // when
            article.removeTag(tag)

            // then
            article.tags.size shouldBe 0
        }

        @Test
        fun `없는 태그를 삭제하려고 하면 예외가 발생한다`() {
            // given
            val tag = Tag.newOne(TagName("tag"))
            val article = ArticleTestFixtures.createPublishedArticle()

            // when, then
            shouldThrow<IllegalArgumentException> {
                article.removeTag(tag)
            }
        }

        @Test
        fun `삭제된 게시글은 태그를 삭제할 수 없다`() {
            // given
            val tag = Tag.newOne(TagName("tag"))
            val article = ArticleTestFixtures.createDeletedArticle()

            // when, then
            shouldThrow<IllegalStateException> {
                article.removeTag(tag)
            }
        }
    }
}
