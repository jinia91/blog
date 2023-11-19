package kr.co.jiniaslog.blog.adapter.out.rdb.articleview

import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleStatus
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.articleview.ArticleView
import kr.co.jiniaslog.blog.domain.category.CategoryName
import kr.co.jiniaslog.blog.domain.writer.WriterName
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPM
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("article_view")
class ArticleViewPM(
    @Id
    override val id: Long,
    var writerName: String,
    var title: String?,
    var thumbnailUrl: String?,
    var categoryName: String?,
    var content: String?,
    var status: ArticleStatus,
    override var createdAt: LocalDateTime?,
    override var updatedAt: LocalDateTime?,
) : AbstractPM() {
    fun toDomain() =
        ArticleView.from(
            id = ArticleId(id),
            writer = WriterName(writerName),
            title = title?.let { ArticleTitle(it) },
            thumbnailUrl = thumbnailUrl?.let { ArticleThumbnailUrl(it) },
            categoryName = categoryName?.let { CategoryName(it) },
            content = content?.let { ArticleContent(it) },
            status = status,
            updatedAt = updatedAt,
            createdAt = createdAt,
        )
}

internal fun ArticleView.toPM() =
    ArticleViewPM(
        id = this.id.value,
        writerName = this.writer.value,
        title = this.title?.value,
        thumbnailUrl = this.thumbnailUrl?.value,
        categoryName = this.categoryName?.value,
        content = this.content?.value,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
