package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.user.UserId

interface IDeleteCategory {
    fun handle(command: Command): Info

    data class Command(
        val categoryId: CategoryId,
    )

    class Info()
}
