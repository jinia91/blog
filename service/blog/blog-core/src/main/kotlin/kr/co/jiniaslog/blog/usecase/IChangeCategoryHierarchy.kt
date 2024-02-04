package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.category.CategoryId

interface IChangeCategoryHierarchy {
    fun handle(command: Command): Info

    data class Command(
        val categoryId: CategoryId,
        val parent: CategoryId?,
        val sortingPoint: Int,
    )

    data class Info(
        val id: CategoryId,
    )
}
