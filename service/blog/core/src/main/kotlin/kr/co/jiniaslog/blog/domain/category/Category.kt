package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import java.time.LocalDateTime

class Category private constructor(
    id: CategoryId,
    name: CategoryName,
    order: SortingOrder,
    parentCategoryId: CategoryId?,
) : AggregateRoot<CategoryId>(), Comparable<Category> {
    override val id: CategoryId = id
    var name: CategoryName = name
        private set

    var order: SortingOrder = order
        private set

    var parentCategoryId: CategoryId? = parentCategoryId
        private set

    val tier: CategoryTier
        get() = parentCategoryId.takeIf { it != null }?.let { CategoryTier.CHILD } ?: CategoryTier.PARENT

    fun update(
        name: CategoryName = this.name,
        order: SortingOrder = this.order,
        parentCategoryId: CategoryId? = this.parentCategoryId,
    ): Category {
        this.name = name
        this.order = order
        this.parentCategoryId = parentCategoryId
        return this
    }

    override fun compareTo(other: Category): Int {
        return compareValuesBy(this, other, { it.tier }, { it.order })
    }

    companion object {
        fun from(
            id: CategoryId,
            name: CategoryName,
            order: SortingOrder,
            parentCategoryId: CategoryId?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Category {
            return Category(
                id = id,
                name = name,
                order = order,
                parentCategoryId = parentCategoryId,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }

        fun sortByHierarchy(categories: List<Category>): List<Category> {
            val sortedCategories = mutableListOf<Category>()

            val parentCategories =
                categories.filter { it.parentCategoryId == null }
                    .sortedWith(compareBy { it.order })

            for (parent in parentCategories) {
                sortedCategories.add(parent)
                val childCategories =
                    categories.filter { it.parentCategoryId == parent.id }
                        .sortedWith(compareBy { it.order })
                sortedCategories.addAll(childCategories)
            }

            return sortedCategories
        }

        fun create(
            id: CategoryId,
            name: CategoryName,
            order: SortingOrder,
            parentCategoryId: CategoryId?,
        ): Category {
            return Category(
                id = id,
                name = name,
                order = order,
                parentCategoryId = parentCategoryId,
            )
        }
    }
}

fun List<Category>.sortByHierarchy(): List<Category> {
    return Category.sortByHierarchy(this)
}
