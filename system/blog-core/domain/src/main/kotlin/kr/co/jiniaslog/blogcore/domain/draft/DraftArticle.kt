package kr.co.jiniaslog.blogcore.domain.draft

import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.DomainEntity

class DraftArticle private constructor(
    id: DraftArticleId,
    writerId: UserId,
    title: String?,
    content: String?,
    thumbnailUrl: String?,
    categoryId: CategoryId?,
    tags: Set<TagId>,
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

    object Factory {
        fun from(
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
        )
    }
}
