package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.TagName

interface IRemoveTagInArticle {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
        val tagName: TagName
    )

    data class Info(
        val articleId: ArticleId,
    )
}
