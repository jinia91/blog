package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.shared.core.domain.DomainEntity
import java.time.LocalDateTime

class Category(
    id: CategoryId,
    name: CategoryName,
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
) : DomainEntity<CategoryId>(createdAt, updatedAt) {
    override val id: CategoryId = id
    var name: CategoryName = name; private set
}
