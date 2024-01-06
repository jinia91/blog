package kr.co.jiniaslog.user.domain.auth

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class AccessToken(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "AccessToken must not be blank" }
    }
}
