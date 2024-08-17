package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId

interface IUpdateArticleContents {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
        val articleContents: ArticleContents
    )

    data class Info(
        val articleId: ArticleId
    )
}
