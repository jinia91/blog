package kr.co.jiniaslog.blogcore.domain.category

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import java.time.LocalDateTime

class Category private constructor(
    id: CategoryId,
    label: String,
    parentId: CategoryId?,
    order: Int,
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
) : AggregateRoot<CategoryId>(createdAt, updatedAt) {
    override val id: CategoryId = id

    var label: String = label
        private set

    var parentId: CategoryId? = parentId
        private set

    var order: Int = order
        private set

    fun reLabeling(label: String) {
        this.label = label
    }

    companion object Factory {
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
