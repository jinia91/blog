package kr.co.jiniaslog.blog.domain

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.Tagging
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

object ArticleTestFixtures {
    fun createPublishedArticle(
        id: ArticleId = ArticleId(IdUtils.generate()),
        memoRefId: MemoId? = MemoId(IdUtils.generate()),
        authorId: UserId = UserId(1L),
        title: String = "title",
        contents: String = "contents",
        thumbnailUrl: String = "thumbnailUrl",
        tags: List<TagId> =
            listOf(
                TagId(IdUtils.generate()),
            ),
        status: Article.Status = Article.Status.PUBLISHED,
        hit: Int = 0,
        createdAt: LocalDateTime? = null,
        updatedAt: LocalDateTime? = null,
    ): Article {
        val articleContents =
            ArticleContents(
                title = title,
                contents = contents,
                thumbnailUrl = thumbnailUrl,
            )

        return Article(
            memoRefId = memoRefId,
            authorId = authorId,
            articleContents = articleContents,
            tags = tags.map { Tagging(it) }.toMutableSet(),
            hit = hit,
            status = status,
            id = id,
        ).apply {
            this.createdAt = createdAt
            this.updatedAt = updatedAt
        }
    }

    fun createDeletedArticle(
        id: ArticleId = ArticleId(IdUtils.generate()),
        memoRefId: MemoId? = MemoId(IdUtils.generate()),
        authorId: UserId = UserId(1L),
        title: String = "title",
        contents: String = "contents",
        thumbnailUrl: String = "thumbnailUrl",
        status: Article.Status = Article.Status.DELETED,
        hit: Int = 0,
        createdAt: LocalDateTime? = null,
        updatedAt: LocalDateTime? = null,
    ): Article {
        return Article(
            memoRefId = memoRefId,
            authorId = authorId,
            articleContents = ArticleContents(
                title = title,
                contents = contents,
                thumbnailUrl = thumbnailUrl,
            ),
            tags = mutableSetOf(),
            hit = hit,
            status = status,
            id = id,
        ).apply {
            this.createdAt = createdAt
            this.updatedAt = updatedAt
        }
    }

    fun createDraftArticle(
        id: ArticleId = ArticleId(IdUtils.generate()),
        memoRefId: MemoId? = null,
        authorId: UserId = UserId(1L),
        title: String = "",
        contents: String = "",
        thumbnailUrl: String = "",
        tags: List<TagId> = emptyList(),
        status: Article.Status = Article.Status.DRAFT,
        hit: Int = 0,
        createdAt: LocalDateTime? = null,
        updatedAt: LocalDateTime? = null,
    ): Article {
        return Article(
            memoRefId = memoRefId,
            authorId = authorId,
            articleContents = ArticleContents(
                title = title,
                contents = contents,
                thumbnailUrl = thumbnailUrl,
            ),
            tags = tags.map { Tagging(it) }.toMutableSet(),
            hit = hit,
            status = status,
            id = id,
        ).apply {
            this.createdAt = createdAt
            this.updatedAt = updatedAt
        }
    }
}
