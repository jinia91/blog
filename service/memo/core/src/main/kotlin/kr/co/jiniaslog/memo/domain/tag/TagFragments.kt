package kr.co.jiniaslog.memo.domain.tag

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class TagId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "태그 아이디는 0보다 커야 합니다." }
    }
}

@JvmInline
value class TagName(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.length in 1..20) { "태그 이름은 1자 이상 20자 이하여야 합니다." }
    }
}
