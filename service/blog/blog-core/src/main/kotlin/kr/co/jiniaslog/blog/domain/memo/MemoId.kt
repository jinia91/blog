package kr.co.jiniaslog.blog.domain.memo

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class MemoId(val id: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(id > 0) { "id must be positive" }
    }
}
