package kr.co.jiniaslog.blog.adapter.inbound.http.dto

import kr.co.jiniaslog.blog.adapter.inbound.http.viewmodel.CategoryViewModel
import kr.co.jiniaslog.blog.usecase.category.ISyncCategories

data class SyncCategoriesRequest(
    val categories: List<CategoryViewModel>,
) {
    fun toCommand(): ISyncCategories.Command {
        return ISyncCategories.Command(categories.map { it.toCategoryDataHolder() })
    }
}
