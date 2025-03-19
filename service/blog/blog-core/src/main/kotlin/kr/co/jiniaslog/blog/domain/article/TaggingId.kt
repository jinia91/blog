package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class TaggingId(val value: Long) : ValueObject, Serializable {
    init {
        validate()
    }
    override fun validate() {
        require(value > 0) { "id must be positive" }
    }
}
