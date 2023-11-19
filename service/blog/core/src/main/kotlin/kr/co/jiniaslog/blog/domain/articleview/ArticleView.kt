package kr.co.jiniaslog.blog.domain.articleview

import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.category.CategoryName
import kr.co.jiniaslog.blog.domain.writer.WriterName
import kr.co.jiniaslog.shared.core.domain.AggregateRoot

class ArticleView private constructor(
    id: ArticleId,
    title: ArticleTitle?,
    writer: WriterName,
    thumbnailUrl: ArticleThumbnailUrl?,
    categoryName: CategoryName?,
    content: ArticleContent?,
) : AggregateRoot<ArticleId>() {
    override val id: ArticleId = id
    var writer: WriterName? = writer
        private set
    var title: ArticleTitle? = title
        private set
    var thumbnailUrl: ArticleThumbnailUrl? = thumbnailUrl
        private set
    var categoryName: CategoryName? = categoryName
        private set
    var content: ArticleContent? = content
        private set

    fun update(
        title: ArticleTitle?,
        writer: WriterName,
        thumbnailUrl: ArticleThumbnailUrl?,
        categoryName: CategoryName?,
        content: ArticleContent?,
    ): ArticleView {
        this.title = title
        this.writer = writer
        this.thumbnailUrl = thumbnailUrl
        this.categoryName = categoryName
        this.content = content
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
        ): ArticleView {
            return ArticleView(
                id = id,
                title = title,
                writer = writer,
                thumbnailUrl = thumbnailUrl,
                categoryName = categoryName,
                content = content,
            )
        }

        fun create(
            id: ArticleId,
            title: ArticleTitle?,
            writer: WriterName,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryName: CategoryName?,
            content: ArticleContent?,
        ): ArticleView {
            return ArticleView(
                id = id,
                title = title,
                writer = writer,
                thumbnailUrl = thumbnailUrl,
                categoryName = categoryName,
                content = content,
            )
        }
    }
}
