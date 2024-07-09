package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.article.ArticleId

interface IDeleteArticle {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
    )

    class Info(
        val articleId: ArticleId
    )
}
