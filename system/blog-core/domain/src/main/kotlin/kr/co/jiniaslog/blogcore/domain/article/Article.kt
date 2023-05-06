package kr.co.jiniaslog.blogcore.domain.article

import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import java.time.LocalDateTime

class Article private constructor(
    id: ArticleId,
    title: String,
    content: String,
    thumbnailUrl: String,
    userId: UserId,
    categoryId: CategoryId,
    tags: Set<TagId>,
    hit: Long = 0,
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
) : AggregateRoot<ArticleId>(createdAt, updatedAt) {

    override val id: ArticleId = id

    var title: String = title
        private set

    var content: String = content
        private set

    var hit: Long = hit
        private set

    var thumbnailUrl: String = thumbnailUrl
        private set

    val writerId: UserId = userId

    var categoryId: CategoryId = categoryId
        private set

    var tags: Set<TagId> = tags
        private set

    fun edit(title: String, content: String, thumbnailUrl: String, categoryId: CategoryId, tags: Set<TagId>) {
        ArticleValidatePolicy.validate(
            this.writerId,
            title,
            content,
            thumbnailUrl,
            categoryId,
            tags,
        )
        this.title = title
        this.content = content
        this.thumbnailUrl = thumbnailUrl
        this.categoryId = categoryId
        this.tags = tags
    }

    fun hit() {
        this.hit++
    }

    fun registerPublishedArticleDeletedEvent() {
        registerEvent(PublishedArticleDeletedEvent(this.id, this.writerId))
    }

    object ArticleValidatePolicy {
        fun validate(
            writerId: UserId,
            title: String,
            content: String,
            thumbnailUrl: String,
            categoryId: CategoryId,
            tags: Set<TagId>,
        ) {
            if (writerId == UserId(0)) throw ArticleNotValidException("shouldHaveWriter")
            if (title.isBlank()) throw ArticleNotValidException("shouldHaveTitle")
            if (content.isBlank()) throw ArticleNotValidException("shouldHaveContent")
            if (categoryId == CategoryId(0)) throw ArticleNotValidException("shouldHaveCategory")
            if (tags.isEmpty()) throw ArticleNotValidException("shouldHaveTags")
            if (thumbnailUrl.isBlank()) throw ArticleNotValidException("shouldHaveThumbnail")
        }
    }

    object Factory {
        fun newPublishedArticle(
            id: ArticleId,
            writerId: UserId,
            title: String,
            content: String,
            thumbnailUrl: String,
            categoryId: CategoryId,
            tags: Set<TagId>,
            draftArticleId: DraftArticleId?,
        ): Article {
            val newOne = Article(
                id = id,
                userId = writerId,
                title = title,
                content = content,
                thumbnailUrl = thumbnailUrl,
                categoryId = categoryId,
                tags = tags,
                createdAt = null,
                updatedAt = null,
            ).apply {
                ArticleValidatePolicy.validate(
                    this.writerId,
                    this.title,
                    this.content,
                    this.thumbnailUrl,
                    this.categoryId,
                    this.tags,
                )
                registerEvent(PublishedArticleCreatedEvent(this.id, this.writerId, draftArticleId))
            }
            return newOne
        }

        fun from(
            id: ArticleId,
            writerId: UserId,
            title: String,
            content: String,
            hit: Long,
            thumbnailUrl: String,
            categoryId: CategoryId,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Article = Article(
            id = id,
            title = title,
            content = content,
            hit = hit,
            thumbnailUrl = thumbnailUrl,
            userId = writerId,
            categoryId = categoryId,
            tags = emptySet(),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
