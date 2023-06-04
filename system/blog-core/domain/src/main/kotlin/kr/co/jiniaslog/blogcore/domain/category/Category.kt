package kr.co.jiniaslog.blogcore.domain.category

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import java.time.LocalDateTime

class Category private constructor(
    id: CategoryId,
    label: String,
    parent: CategoryId?,
    displayOrder: Int,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : AggregateRoot<CategoryId>(createdAt, updatedAt) {
    override val id: CategoryId = id

    var label: String = label
        private set

    var parent: CategoryId? = parent
        private set

    var displayOrder: Int = displayOrder
        private set

    fun reLabeling(label: String) {
        this.label = label
    }
}
