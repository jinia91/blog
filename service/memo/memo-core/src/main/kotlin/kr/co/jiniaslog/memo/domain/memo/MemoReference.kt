package kr.co.jiniaslog.memo.domain.memo

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Table
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
@Table(name = "memo_reference")
data class MemoReference(
    @AttributeOverride(column = Column(name = "root_id"), name = "value")
    val rootId: MemoId,
    @AttributeOverride(column = Column(name = "reference_id"), name = "value")
    val referenceId: MemoId,
) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(rootId != referenceId) { "rootId and referenceId must be different" }
    }
}
