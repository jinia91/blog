package kr.co.jiniaslog.blog.domain.category

import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class CategoryId(
    val id: Long,
) : ValueObject {
    @PrePersist
    @PreUpdate
    override fun validate() {
        require(id > 0) { "id must be positive" }
    }
}
