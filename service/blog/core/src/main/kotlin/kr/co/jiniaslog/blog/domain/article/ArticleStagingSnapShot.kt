package kr.co.jiniaslog.blog.domain.article

import java.time.LocalDateTime
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.shared.core.domain.DomainEntity

class ArticleStagingSnapShot(
    override val id: ArticleId,
) : DomainEntity<ArticleId>() {
    var title: ArticleTitle? = null
        private set
    var content: ArticleContent? = null
        private set
    var thumbnailUrl: ArticleThumbnailUrl? = null
        private set
    var categoryId: CategoryId? = null
        private set

    companion object {
        fun capture(
            id: ArticleId,
            title: ArticleTitle?,
            content: ArticleContent?,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryId: CategoryId?,
        ): ArticleStagingSnapShot {
            return ArticleStagingSnapShot(
                id = id,
            ).apply {
                this.title = title
                this.content = content
                this.thumbnailUrl = thumbnailUrl
                this.categoryId = categoryId
            }
        }

        fun from(
            id: ArticleId,
            title: ArticleTitle?,
            content: ArticleContent?,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryId: CategoryId?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): ArticleStagingSnapShot {
            return ArticleStagingSnapShot(
                id = id,
            ).apply {
                this.title = title
                this.content = content
                this.thumbnailUrl = thumbnailUrl
                this.categoryId = categoryId
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}