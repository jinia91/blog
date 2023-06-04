package kr.co.jiniaslog.blogcore.application.category.usecase

import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import java.time.LocalDateTime

interface CategoryCommands {
    fun syncCategories(command: SyncCategoryCommand)

    data class SyncCategoryCommand(
        val categoryVos: List<CategoryVo>,
    )

    data class CategoryVo(
        val id: CategoryId?,
        val label: String,
        val parentId: CategoryId,
        val order: Int,
        val createAt: LocalDateTime?,
        val updatedAt: LocalDateTime?,
    )
}
