package kr.co.jiniaslog.blogcore.application.category.usecase

import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import java.time.LocalDateTime

interface CategoryCommands {
    fun syncCategories(command: SyncCategoryCommand)

    data class SyncCategoryCommand(
        val categoriesData: List<CategoryData>,
    )

    data class CategoryData(
        val id: CategoryId?,
        val label: String,
        val parentId: CategoryId?,
        val order: Int,
        val createAt: LocalDateTime?,
        val updatedAt: LocalDateTime?,
    ) {
        val isNew: Boolean
            get() = id == null
    }
}
