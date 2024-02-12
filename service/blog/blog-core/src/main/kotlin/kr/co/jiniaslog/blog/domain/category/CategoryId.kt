package kr.co.jiniaslog.blog.domain.category

import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class CategoryId(
    val value: Long,
) : ValueObject {
    override fun validate() {
        require(value > 0) { "id must be positive" }
    }
}
