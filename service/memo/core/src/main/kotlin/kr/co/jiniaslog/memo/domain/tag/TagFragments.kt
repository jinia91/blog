package kr.co.jiniaslog.memo.domain.tag

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class TagId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        if (value < 0) {
            throw IllegalArgumentException("태그 ID는 0 이상이어야 합니다.")
        }
    }
}

@JvmInline
value class TagName(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        if (value.isEmpty()) {
            throw IllegalArgumentException("태그 이름은 1자 이상이어야 합니다.")
        }
    }
}
