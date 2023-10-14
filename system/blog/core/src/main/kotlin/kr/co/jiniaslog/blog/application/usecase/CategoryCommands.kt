package kr.co.jiniaslog.blog.application.usecase

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.shared.core.domain.Command

interface CategoryCommands {
    fun syncCategories(command: SyncCategoryCommand)

    data class SyncCategoryCommand(
        val categoriesData: List<CategoryData>,
    ) : Command(false)

    data class CategoryData(
        val id: CategoryId?,
        val label: String,
        val parentId: CategoryId?,
        val order: Int,
    ) {
        val isNew: Boolean
            get() = id == null
    }
}
