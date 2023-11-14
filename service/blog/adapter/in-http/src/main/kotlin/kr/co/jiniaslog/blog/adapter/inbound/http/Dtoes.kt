package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.article.WriterId
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.usecase.ArticleCommitCommandUseCase.ArticleCommitCommand
import kr.co.jiniaslog.blog.usecase.ArticleCommitCommandUseCase.CommitInfo
import kr.co.jiniaslog.blog.usecase.ArticleInitCommandUseCase.ArticleInitCommand
import kr.co.jiniaslog.blog.usecase.ArticleInitCommandUseCase.InitialInfo
import kr.co.jiniaslog.blog.usecase.ArticleStagingCommandUseCase.ArticleStagingCommand
import kr.co.jiniaslog.blog.usecase.ArticleStagingCommandUseCase.StagingInfo

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

data class ArticleStagingRequest(
    val articleId: Long,
    val title: String?,
    val content: String?,
    val thumbnailUrl: String?,
    val categoryId: Long?,
) {
    fun toCommand() =
        ArticleStagingCommand(
            articleId = ArticleId(articleId),
            title = title?.let { ArticleTitle(it) },
            content = content?.let { ArticleContent(it) },
            thumbnailUrl = thumbnailUrl?.let { ArticleThumbnailUrl(it) },
            categoryId = categoryId?.let { CategoryId(it) },
        )
}

data class ArticleStagingResponse(
    val articleId: Long,
)

fun StagingInfo.toResponse() = ArticleStagingResponse(this.articleId.value)
