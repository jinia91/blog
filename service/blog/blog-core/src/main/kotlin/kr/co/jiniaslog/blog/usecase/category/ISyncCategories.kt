package kr.co.jiniaslog.blog.usecase.category

import kr.co.jiniaslog.blog.domain.category.dto.CategoriesHolder

interface ISyncCategories {
    fun handle(command: Command): Info

    data class Command(val toSyncCategories: CategoriesHolder)

    class Info()
}
