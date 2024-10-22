package kr.co.jiniaslog.memo.domain.memo

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class AuthorId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "작성자 ID는 0 초과여야 합니다." }
    }
}
