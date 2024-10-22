package kr.co.jiniaslog.memo.domain.folder

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class FolderId(val value: Long) : ValueObject, Serializable {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "FolderId는 0 초과여야 합니다." }
    }
}
