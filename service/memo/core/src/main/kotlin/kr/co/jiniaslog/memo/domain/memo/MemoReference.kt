package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.ValueObject

data class MemoReference(
    val rootId: MemoId,
    val referenceId: MemoId,
) : ValueObject {
    init {
        validate()
    }

    override fun validate() {}
}
