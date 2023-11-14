package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.shared.core.domain.DomainEntity
import java.time.LocalDateTime

class ArticleStagingSnapShot(
    override val id: StagingSnapShotId,
    val articleId: ArticleId,
    val title: ArticleTitle? = null,
    val content: ArticleContent,
    val thumbnailUrl: ArticleThumbnailUrl? = null,
    val categoryId: CategoryId? = null,
) : DomainEntity<StagingSnapShotId>() {
    companion object {
        fun capture(
            id: StagingSnapShotId,
            articleId: ArticleId,
            title: ArticleTitle?,
            content: ArticleContent?,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryId: CategoryId?,
        ): ArticleStagingSnapShot {
            return ArticleStagingSnapShot(
                id = id,
                title = title,
                articleId = articleId,
                content = content ?: ArticleContent.EMPTY,
                thumbnailUrl = thumbnailUrl,
                categoryId = categoryId,
            )
        }

        fun from(
            id: StagingSnapShotId,
            articleId: ArticleId,
            title: ArticleTitle?,
            content: ArticleContent,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryId: CategoryId?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): ArticleStagingSnapShot {
            return ArticleStagingSnapShot(
                id = id,
                articleId = articleId,
                title = title,
                content = content,
                thumbnailUrl = thumbnailUrl,
                categoryId = categoryId,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
