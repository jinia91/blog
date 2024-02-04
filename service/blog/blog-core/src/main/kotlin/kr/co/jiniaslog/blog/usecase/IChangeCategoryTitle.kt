package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryTitle

interface IChangeCategoryTitle {
    fun handle(command: Command): Info

    data class Command(
        val categoryId: CategoryId,
        val categoryTitle: CategoryTitle,
    )

    data class Info(
        val id: CategoryId
    )
}
