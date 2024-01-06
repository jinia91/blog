package kr.co.jiniaslog.user.domain.user

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class NickName(
    val value: String,
) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "닉네임은 빈 값이 될 수 없습니다." }
    }
}
