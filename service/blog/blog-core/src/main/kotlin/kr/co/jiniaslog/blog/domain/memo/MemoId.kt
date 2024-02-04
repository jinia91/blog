package kr.co.jiniaslog.blog.domain.memo

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class MemoId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "id must be positive" }
    }
}
