package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryTitle

interface ICreateNewCategory {
    fun handle(command: Command): Info

    data class Command(
        val title: CategoryTitle,
        val parentId: CategoryId?,
        val sortingPoint: Int,
    )

    data class Info(
        val id: CategoryId,
    )
}
