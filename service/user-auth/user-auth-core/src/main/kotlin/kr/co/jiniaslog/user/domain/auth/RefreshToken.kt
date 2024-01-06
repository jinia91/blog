package kr.co.jiniaslog.user.domain.auth

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class RefreshToken(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "Refresh must not be blank" }
    }
}
