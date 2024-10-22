package kr.co.jiniaslog.memo.domain.memo

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class MemoReference(
    val rootId: MemoId,
    val referenceId: MemoId,
) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(rootId != referenceId) { "rootId and referenceId must be different" }
    }
}
