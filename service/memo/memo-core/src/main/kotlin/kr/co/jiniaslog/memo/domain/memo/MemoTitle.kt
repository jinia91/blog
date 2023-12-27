package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class MemoTitle(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {}
}
