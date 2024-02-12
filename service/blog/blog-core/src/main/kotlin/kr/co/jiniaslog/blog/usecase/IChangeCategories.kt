package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.category.SimpleCategoryVo

interface IChangeCategories {
    fun handle(command: Command): Info

    data class Command(
        val requestedFlattenedCategoryData: List<SimpleCategoryVo>,
    )

    class Info()
}
