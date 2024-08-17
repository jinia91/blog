package kr.co.jiniaslog.blog.adapter.inbound.http.viewmodel

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryTitle
import kr.co.jiniaslog.blog.domain.category.dto.CategoryDataHolder

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
