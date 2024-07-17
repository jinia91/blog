package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryTitle
import kr.co.jiniaslog.blog.domain.category.dto.CategoryDataHolder
import kr.co.jiniaslog.blog.usecase.category.ISyncCategories

data class SyncCategoriesRequest(
    val categories: List<CategoryViewModel>,
) {
    fun toCommand(): ISyncCategories.Command {
        return ISyncCategories.Command(categories.map { it.toCategoryDataHolder() })
    }
}

data class CategoryViewModel(
    val categoryId: Long,
    val categoryTitle: String,
    val children: List<CategoryViewModel>,
    val sortingPoint: Int,
) {
    fun toCategoryDataHolder(): CategoryDataHolder {
        return CategoryDataHolder(
            categoryId = CategoryId(categoryId),
            categoryName = CategoryTitle(categoryTitle),
            children = children.map { it.toCategoryDataHolder() },
            sortingPoint = sortingPoint
        )
    }
}
