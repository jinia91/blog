package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.shared.core.domain.AggregateRoot

class Category private constructor(
    id: CategoryId,
    name: CategoryName,
    order: SortingOrder,
    parentCategory: Category?,
) : AggregateRoot<CategoryId>(), Comparable<Category> {
    override val id: CategoryId = id
    var name: CategoryName = name
        private set

    var order: SortingOrder = order
        private set

    var parentCategory: Category? = parentCategory
        private set

    val tier: CategoryTier
        get() = parentCategory.takeIf { it != null }?.let { CategoryTier.CHILD } ?: CategoryTier.PARENT

    fun update(
        name: CategoryName = this.name,
        order: SortingOrder = this.order,
        parentCategory: Category? = this.parentCategory,
    ): Category {
        this.name = name
        this.order = order
        this.parentCategory = parentCategory
        return this
    }

    override fun compareTo(other: Category): Int {
        return compareValuesBy(this, other, { it.tier }, { it.order })
    }

    companion object {
        fun sortByHierarchy(categories: List<Category>): List<Category> {
            val sortedCategories = mutableListOf<Category>()

            val parentCategories =
                categories.filter { it.parentCategory == null }
                    .sortedWith(compareBy { it.order })

            for (parent in parentCategories) {
                sortedCategories.add(parent)
                val childCategories =
                    categories.filter { it.parentCategory == parent }
                        .sortedWith(compareBy { it.order })
                sortedCategories.addAll(childCategories)
            }

            return sortedCategories
        }

        fun create(
            id: CategoryId,
            name: CategoryName,
            order: SortingOrder,
            parentCategory: Category?,
        ): Category {
            return Category(
                id = id,
                name = name,
                order = order,
                parentCategory = parentCategory,
            )
        }
    }
}

fun List<Category>.sortByHierarchy(): List<Category> {
    return Category.sortByHierarchy(this)
}
