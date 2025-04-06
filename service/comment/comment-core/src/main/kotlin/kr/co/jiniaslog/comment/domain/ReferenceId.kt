package kr.co.jiniaslog.comment.domain

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class ReferenceId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "ReferenceId must be positive" }
    }
}
