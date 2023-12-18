package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class MemoContent(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {}
}

@JvmInline
value class MemoId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "메모 ID는 0 초과여야합니다" }
    }
}

@JvmInline
value class MemoTitle(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {}
}

@JvmInline
value class AuthorId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "작성자 ID는 0 초과여야 합니다." }
    }
}
