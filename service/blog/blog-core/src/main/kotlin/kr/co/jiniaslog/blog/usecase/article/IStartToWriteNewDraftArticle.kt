package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.UserId
import kr.co.jiniaslog.blog.domain.article.ArticleId

interface IStartToWriteNewDraftArticle {
    fun handle(command: Command): Info

    data class Command(val authorId: UserId)

    data class Info(
        val articleId: ArticleId,
    )
}
