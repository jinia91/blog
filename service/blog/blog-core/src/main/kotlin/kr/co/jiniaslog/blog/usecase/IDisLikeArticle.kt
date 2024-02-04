package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.user.UserId

interface IDisLikeArticle {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
        val userId: UserId,
    )

    data class Info(
        val id: ArticleId,
    )
}
