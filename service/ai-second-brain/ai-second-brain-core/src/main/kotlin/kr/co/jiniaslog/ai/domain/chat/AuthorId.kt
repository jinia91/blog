package kr.co.jiniaslog.ai.domain.chat

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class AuthorId(
    val value: Long,
) : ValueObject {
    init {
        require(value > 0) { "AuthorId must be positive" }
    }

    override fun validate() {
        require(value > 0) { "AuthorId must be positive" }
    }
}
