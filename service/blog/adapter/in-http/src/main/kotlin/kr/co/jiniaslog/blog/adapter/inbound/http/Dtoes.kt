package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.article.WriterId
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.usecase.ArticleCommitCommand
import kr.co.jiniaslog.blog.usecase.ArticleInitCommand
import kr.co.jiniaslog.blog.usecase.CommitInfo
import kr.co.jiniaslog.blog.usecase.InitialInfo

data class ArticleInitRequest(
    val writerId: Long,
) {
    fun toCommand() = ArticleInitCommand(WriterId(writerId))
}

data class ArticleInitResponse(
    val articleId: Long,
)

fun InitialInfo.toResponse() = ArticleInitResponse(this.articleId.value)

data class ArticleCommitRequest(
    val articleId: Long,
    val title: String?,
    val content: String,
    val thumbnailUrl: String?,
    val categoryId: Long?,
) {
    fun toCommand() =
        ArticleCommitCommand(
            articleId = ArticleId(articleId),
            title = title?.let { ArticleTitle(it) },
            content = ArticleContent(content),
            thumbnailUrl = thumbnailUrl?.let { ArticleThumbnailUrl(thumbnailUrl) },
            categoryId = categoryId?.let { CategoryId(it) },
        )
}

data class ArticleCommitResponse(
    val articleId: Long,
    val commitId: Long,
)

fun CommitInfo.toResponse() = ArticleCommitResponse(this.articleId.value, this.commitId.value)
