package kr.co.jiniaslog.blog.domain
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.time.LocalDateTime

val constructor: Constructor<Article> = Article::class.java.getDeclaredConstructor(
    ArticleId::class.java,
    MemoId::class.java,
    UserId::class.java,
    Article.Status::class.java,
    ArticleContents::class.java,
    ArticleContents::class.java,
    MutableSet::class.java,
    Int::class.javaPrimitiveType,
).apply {
    isAccessible = true
}

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

        val article = try {
            constructor.newInstance(
                id,
                memoRefId,
                authorId,
                status,
                articleContents,
                articleContents,
                mutableSetOf<Tag>(),
                hit,
            )
        } catch (e: InvocationTargetException) {
            throw IllegalArgumentException("리플렉션으로 생성 실패", e)
        }

        article.createdAt = createdAt
        article.updatedAt = updatedAt
        tags.forEach { article.addTag(it) }

        return article
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
        val article = try {
            constructor.newInstance(
                id,
                memoRefId,
                authorId,
                status,
                ArticleContents.EMPTY,
                ArticleContents(
                    title = title,
                    contents = contents,
                    thumbnailUrl = thumbnailUrl,
                ),
                mutableSetOf<Tag>(),
                hit,
            )
        } catch (e: InvocationTargetException) {
            throw IllegalArgumentException("리플렉션으로 생성 실패", e)
        }

        return article.apply {
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
        val articleContents = ArticleContents(
            title = title,
            contents = contents,
            thumbnailUrl = thumbnailUrl,
        )

        val article = try {
            constructor.newInstance(
                id,
                memoRefId,
                authorId,
                status,
                ArticleContents.EMPTY,
                articleContents,
                mutableSetOf<Tag>(),
                hit,
            )
        } catch (e: InvocationTargetException) {
            throw IllegalArgumentException("리플렉션으로 생성 실패", e)
        }

        article.createdAt = createdAt
        article.updatedAt = updatedAt
        tags.forEach { article.addTag(it) }

        return article
    }
}
