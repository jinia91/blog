package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId

interface ArticleStatusChangeFacade :
    IPublishArticle,
    IUnPublishArticle,
    IDeleteArticle,
    IUnDeleteArticle {
    fun determineCommand(asIsStatus: Article.Status, toBeStatus: Article.Status, articleId: ArticleId): Command
    fun handle(command: Command): Info

    interface Command

    interface Info {
        val articleId: ArticleId
    }
}
