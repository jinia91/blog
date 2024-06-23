package kr.co.jiniaslog.user.domain.auth.token

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class AuthorizationCode (val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "인증 코드는 빈 값이 될 수 없습니다." }
    }
}
