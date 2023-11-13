package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.domain.article.WriterId
import kr.co.jiniaslog.blog.usecase.ArticleInitCommand
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
