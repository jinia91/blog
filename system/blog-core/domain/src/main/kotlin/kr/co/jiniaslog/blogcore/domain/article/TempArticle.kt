package kr.co.jiniaslog.blogcore.domain.article

import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.shared.core.context.DomainEntity

@DomainEntity
class TempArticle private constructor(
    title: String?,
    content: String?,
    thumbnailUrl: String?,
    writerId: UserId,
    categoryId: CategoryId?,
    tags: Set<TagId>,
) {
    val id: ArticleId = ArticleId(TEMP_ARTICLE_STATIC_ID)

    var title: String? = title
        private set

    var content: String? = content
        private set

    var thumbnailUrl: String? = thumbnailUrl
        private set

    val writerId: UserId = writerId

    var categoryId: CategoryId? = categoryId
        private set

    var tags: Set<TagId> = tags
        private set

    object Factory {
        fun newTempOne(
            userId: UserId,
            title: String? = null,
            content: String? = null,
            thumbnailUrl: String? = null,
            categoryId: CategoryId? = null,
            tags: Set<TagId>,
        ): TempArticle = TempArticle(
            writerId = userId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
            tags = tags,
        )
    }

    companion object {
        const val TEMP_ARTICLE_STATIC_ID = 1L
    }
}
