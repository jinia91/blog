package kr.co.jiniaslog.blog.domain.articleview

import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleStatus
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.category.CategoryName
import kr.co.jiniaslog.blog.domain.writer.WriterName
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import java.time.LocalDateTime

class ArticleView private constructor(
    id: ArticleId,
    title: ArticleTitle?,
    writer: WriterName,
    thumbnailUrl: ArticleThumbnailUrl?,
    categoryName: CategoryName?,
    content: ArticleContent?,
    status: ArticleStatus,
) : AggregateRoot<ArticleId>() {
    override val id: ArticleId = id
    var writer: WriterName = writer
        private set
    var title: ArticleTitle? = title
        private set
    var thumbnailUrl: ArticleThumbnailUrl? = thumbnailUrl
        private set
    var categoryName: CategoryName? = categoryName
        private set
    var content: ArticleContent? = content
        private set

    var status: ArticleStatus = status
        private set

    fun update(
        title: ArticleTitle?,
        writer: WriterName,
        thumbnailUrl: ArticleThumbnailUrl?,
        categoryName: CategoryName?,
        content: ArticleContent?,
        status: ArticleStatus,
    ): ArticleView {
        this.title = title
        this.writer = writer
        this.thumbnailUrl = thumbnailUrl
        this.categoryName = categoryName
        this.content = content
        this.status = status
        return this
    }

    companion object {
        fun from(
            id: ArticleId,
            title: ArticleTitle?,
            writer: WriterName,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryName: CategoryName?,
            content: ArticleContent?,
            status: ArticleStatus,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): ArticleView {
            return ArticleView(
                id = id,
                title = title,
                writer = writer,
                thumbnailUrl = thumbnailUrl,
                categoryName = categoryName,
                content = content,
                status = status,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }

        fun create(
            id: ArticleId,
            title: ArticleTitle?,
            writer: WriterName,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryName: CategoryName?,
            content: ArticleContent?,
            status: ArticleStatus,
        ): ArticleView {
            return ArticleView(
                id = id,
                title = title,
                writer = writer,
                thumbnailUrl = thumbnailUrl,
                categoryName = categoryName,
                content = content,
                status = status,
            )
        }
    }
}
