package kr.co.jiniaslog.blog.domain

import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryTitle
import kr.co.jiniaslog.blog.domain.category.dto.CategoryDto
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

object CategoryTestFixtures {
    fun createCategory(
        categoryId: CategoryId = CategoryId(IdUtils.generate()),
        categoryTitle: CategoryTitle = CategoryTitle("카테고리"),
        sortingPoint: Int = 0,
        parent: Category? = null,
        createdAt: LocalDateTime? = null,
        updatedAt: LocalDateTime? = null,
    ): Category {
        return Category(
            id = categoryId,
            categoryTitle = categoryTitle,
            sortingPoint = sortingPoint,
            parent = parent
        ).apply {
            this.createdAt = createdAt
            this.updatedAt = updatedAt
        }
    }
}

fun Category.toDto(): CategoryDto = CategoryDto(
    id = this.entityId,
    title = this.categoryTitle,
    parent = this.parent?.toDto(),
    children = this.children.map { it.toDto() },
    sortingPoint = this.sortingPoint
)
