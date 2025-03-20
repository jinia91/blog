package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.article.ArticleId

interface IUnDeleteArticle {
    fun handle(command: Command): Info

    data class Command(val articleId: ArticleId) : ArticleStatusChangeFacade.Command

    data class Info(override val articleId: ArticleId) : ArticleStatusChangeFacade.Info
}
