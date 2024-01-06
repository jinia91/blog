package kr.co.jiniaslog.user.domain.user

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class Email(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "이메일은 빈 값이 될 수 없습니다." }
    }
}
