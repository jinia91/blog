package kr.co.jiniaslog.blog.usecase.category

import kr.co.jiniaslog.blog.domain.category.dto.CategoryDataHolder

interface ISyncCategories {
    fun handle(command: Command): Info

    data class Command(val toBeSyncCategories: List<CategoryDataHolder>)

    class Info()
}
