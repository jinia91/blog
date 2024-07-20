package kr.co.jiniaslog.blog.domain

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class UserId(val value: Long) : ValueObject, Serializable {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "유저 id는 양수여야 합니다" }
    }
}
