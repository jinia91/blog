package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.category.CategoryId

interface ICategorizeArticle {
    fun handle(command: Command): Info

    data class Command(
        val articleId: ArticleId,
        val categoryId: CategoryId
    )

    data class Info(val articleId: ArticleId)
}
