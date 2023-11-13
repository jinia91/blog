package kr.co.jiniaslog.blog.adapter.out.rdb.article

import kr.co.jiniaslog.blog.domain.article.ArticleCommit
import kr.co.jiniaslog.blog.domain.article.ArticleCommitVersion
import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPM
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "article_commit")
class ArticleCommitPM(
    @Id
    override val id: Long,
    var title: String?,
    var content: String?,
    var thumbnailUrl: String?,
    var articleId: Long,
    var categoryId: Long?,
    override var createdAt: LocalDateTime?,
    override var updatedAt: LocalDateTime?,
) : AbstractPM() {
    fun toEntity() =
        ArticleCommit.from(
            id = ArticleCommitVersion(id),
            title = title?.let { ArticleTitle(it) },
            content = content?.let { ArticleContent(it) },
            thumbnailUrl = thumbnailUrl?.let { ArticleThumbnailUrl(it) },
            categoryId = categoryId?.let { CategoryId(it) },
        )
}

fun ArticleCommit.toPM(articleId: Long): ArticleCommitPM =
    ArticleCommitPM(
        id = this.id.value,
        title = this.title?.value,
        content = this.content?.value,
        thumbnailUrl = this.thumbnailUrl?.value,
        articleId = articleId,
        categoryId = this.categoryId?.value,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
