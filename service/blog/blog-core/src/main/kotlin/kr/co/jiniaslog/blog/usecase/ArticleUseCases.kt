package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.user.UserId

interface ArticleUseCasesFacade :
    IStartToWriteNewArticle,
    IDeleteArticle

interface IStartToWriteNewArticle {
    fun handle(command: Command): Info

    data class Command(val authorId: UserId)

    data class Info(
        val articleId: ArticleId,
    )
}

interface IDeleteArticle {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
    )

    class Info()
}
