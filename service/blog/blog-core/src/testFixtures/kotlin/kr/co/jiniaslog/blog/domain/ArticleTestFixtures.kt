package kr.co.jiniaslog.blog.domain

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

object ArticleTestFixtures {
    fun createPublishedArticle(
        id: ArticleId = ArticleId(IdUtils.generate()),
        memoRefId: MemoId? = MemoId(IdUtils.generate()),
        authorId: UserId = UserId(1L),
        title: String = "title",
        contents: String = "contents",
        tags: List<Tag> = emptyList(),
        thumbnailUrl: String = "thumbnailUrl",
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
            publishedArticleContents = articleContents,
            draftContents = articleContents,
            tags = mutableSetOf(),
            hit = hit,
            status = status,
            id = id,
        ).apply {
            this.createdAt = createdAt
            this.updatedAt = updatedAt
            tags.forEach { addTag(it) }
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
            publishedArticleContents = ArticleContents(
                title = title,
                contents = contents,
                thumbnailUrl = thumbnailUrl,
            ),
            tags = mutableSetOf(),
            hit = hit,
            status = status,
            id = id,
            draftContents = ArticleContents.EMPTY,
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
        tags: List<Tag> = emptyList(),
        status: Article.Status = Article.Status.DRAFT,
        hit: Int = 0,
        createdAt: LocalDateTime? = null,
        updatedAt: LocalDateTime? = null,
    ): Article {
        return Article(
            memoRefId = memoRefId,
            authorId = authorId,
            publishedArticleContents = ArticleContents.EMPTY,
            tags = mutableSetOf(),
            hit = hit,
            status = status,
            id = id,
            draftContents = ArticleContents(
                title = title,
                contents = contents,
                thumbnailUrl = thumbnailUrl,
            ),
        ).apply {
            this.createdAt = createdAt
            this.updatedAt = updatedAt
            tags.forEach { addTag(it) }
        }
    }
}
