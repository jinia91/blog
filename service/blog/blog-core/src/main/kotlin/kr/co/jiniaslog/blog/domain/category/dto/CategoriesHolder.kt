package kr.co.jiniaslog.blog.domain.category.dto

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryTitle

data class CategoriesHolder(
    val categories: List<CategoryDto>
)

data class CategoryDto(
    val id: CategoryId?,
    val title: CategoryTitle,
    val parent: CategoryDto?,
    val children: List<CategoryDto>,
    val sortingPoint: Int
)
