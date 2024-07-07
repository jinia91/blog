package kr.co.jiniaslog.blog.domain.category

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class CategoryId(
    val value: Long,
) : ValueObject, Serializable {
    override fun validate() {
        require(value > 0) { "id must be positive" }
    }
}
