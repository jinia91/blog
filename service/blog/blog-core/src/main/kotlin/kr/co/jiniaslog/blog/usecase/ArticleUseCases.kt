package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.user.UserId

interface ArticleUseCasesFacade :
    IStartToWriteNewArticle,
    IEditArticleContents,
    IDeleteArticle

interface IStartToWriteNewArticle {
    fun handle(command: Command): Info

    data class Command(val authorId: UserId)

    data class Info(
        val articleId: ArticleId,
    )
}

interface IEditArticleContents {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
        val articleContents: ArticleContents,
    )

    data class Info(val id: ArticleId)
}

interface IDeleteArticle {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
    )

    class Info()
}
