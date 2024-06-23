package kr.co.jiniaslog.user.domain.user

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class UserId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "유저 id는 0보다 커야 합니다." }
    }
}
