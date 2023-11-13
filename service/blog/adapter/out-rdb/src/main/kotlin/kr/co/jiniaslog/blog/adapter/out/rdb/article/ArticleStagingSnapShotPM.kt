package kr.co.jiniaslog.blog.adapter.out.rdb.article

import java.time.LocalDateTime
import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleContentDelta
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleStagingSnapShot
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPM
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "article_staging_snap_shot")
class ArticleStagingSnapShotPM(
    @Id
    override val id: Long,
    val title: String?,
    val content: String?,
    val thumbnailUrl: String?,
    val categoryId: Long?,
    override var updatedAt: LocalDateTime?,
    override var createdAt: LocalDateTime?
) : AbstractPM() {
    fun toEntity() = ArticleStagingSnapShot.from(
        id = ArticleId(id),
        title = title?.let { ArticleTitle(it) },
        content = content?.let { ArticleContent(it) },
        thumbnailUrl = thumbnailUrl?.let { ArticleThumbnailUrl(it) },
        categoryId = categoryId?.let { CategoryId(it) },
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

fun ArticleStagingSnapShot.toPM() =
    ArticleStagingSnapShotPM(
        id = this.id.value,
        title = this.title?.value,
        content = this.content?.value,
        thumbnailUrl = this.thumbnailUrl?.value,
        categoryId = this.categoryId?.value,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )