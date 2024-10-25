package kr.co.jiniaslog.memo.domain.memo

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class MemoId(val value: Long) : ValueObject, Serializable {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "메모 ID는 0 초과여야합니다" }
    }
}
