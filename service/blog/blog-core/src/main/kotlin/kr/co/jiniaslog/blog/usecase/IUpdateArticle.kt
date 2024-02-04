package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.user.UserId

interface IUpdateArticle {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
        val articleContents: ArticleContents,
        val tags: List<TagId>,
        val userId: UserId,
    )

    data class Info(
        val id: ArticleId,
    )
}
