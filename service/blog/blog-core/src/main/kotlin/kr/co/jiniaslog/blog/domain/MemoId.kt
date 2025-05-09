package kr.co.jiniaslog.blog.domain

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class MemoId(val value: Long) : ValueObject, Serializable {
    init {
        validate()
    }
    override fun validate() {
        require(value > 0) { "id must be positive" }
    }
}
