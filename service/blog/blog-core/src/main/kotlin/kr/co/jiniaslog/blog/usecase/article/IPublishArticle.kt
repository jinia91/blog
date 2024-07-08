package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.article.ArticleId

interface IPublishArticle {
    fun handle(command: Command): Info

    data class Command(val articleId: ArticleId)

    data class Info(val articleId: ArticleId)
}
