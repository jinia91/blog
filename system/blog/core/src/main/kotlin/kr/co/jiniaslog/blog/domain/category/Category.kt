package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import java.time.LocalDateTime

class Category private constructor(
    id: CategoryId,
    label: String,
    parentId: CategoryId?,
    order: Int,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
) : AggregateRoot<CategoryId>(createdAt, updatedAt) {
    override val id: CategoryId = id

    var label: String = label
        private set

    var parentId: CategoryId? = parentId
        private set

    var order: Int = order
        private set

    val isRoot: Boolean
        get() = parentId == null

    fun update(
        label: String,
        parentId: CategoryId?,
        order: Int,
    ) {
        this.label = label
        this.parentId = parentId
        this.order = order
    }

    companion object Factory {
        fun newOne(
            id: CategoryId,
            label: String,
            parentId: CategoryId?,
            order: Int,
        ): Category {
            return Category(
                id = id,
                label = label,
                parentId = parentId,
                order = order,
                createdAt = null,
                updatedAt = null,
            )
        }

        fun from(
            id: CategoryId,
            label: String,
            parentId: CategoryId?,
            order: Int,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Category {
            return Category(
                id = id,
                label = label,
                parentId = parentId,
                order = order,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }
}
