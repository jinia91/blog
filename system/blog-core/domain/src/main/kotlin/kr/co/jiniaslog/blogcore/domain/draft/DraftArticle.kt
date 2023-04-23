package kr.co.jiniaslog.blogcore.domain.draft

import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.DomainEntity
import java.time.LocalDateTime

class DraftArticle private constructor(
    id: DraftArticleId,
    writerId: UserId,
    title: String?,
    content: String?,
    thumbnailUrl: String?,
    categoryId: CategoryId?,
    tags: Set<TagId>,
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
) : DomainEntity<DraftArticleId>() {

    override val id: DraftArticleId = id

    val writerId: UserId = writerId

    var title: String? = title
        private set

    var content: String? = content
        private set

    var thumbnailUrl: String? = thumbnailUrl
        private set

    var categoryId: CategoryId? = categoryId
        private set

    var tags: Set<TagId> = tags
        private set

    override val createdDate: LocalDateTime? = createdAt

    override val updatedDate: LocalDateTime? = updatedAt

    fun update(
        title: String?,
        content: String?,
        thumbnailUrl: String?,
        categoryId: CategoryId?,
        tags: Set<TagId>,
    ) {
        this.title = title
        this.content = content
        this.thumbnailUrl = thumbnailUrl
        this.categoryId = categoryId
        this.tags = tags
    }

    object Factory {
        fun fromPm(
            id: DraftArticleId,
            writerId: UserId,
            title: String? = null,
            content: String? = null,
            thumbnailUrl: String? = null,
            categoryId: CategoryId? = null,
            tags: Set<TagId> = emptySet(),
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
        ): DraftArticle = DraftArticle(
            id = id,
            writerId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
            tags = tags,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        fun newOne(
            id: DraftArticleId,
            writerId: UserId,
            title: String? = null,
            content: String? = null,
            thumbnailUrl: String? = null,
            categoryId: CategoryId? = null,
            tags: Set<TagId> = emptySet(),
        ): DraftArticle = DraftArticle(
            id = id,
            writerId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
            tags = tags,
            createdAt = null,
            updatedAt = null,
        )
    }
}
