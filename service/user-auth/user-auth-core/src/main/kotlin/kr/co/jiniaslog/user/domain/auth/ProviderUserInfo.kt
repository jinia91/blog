package kr.co.jiniaslog.user.domain.auth

import kr.co.jiniaslog.shared.core.domain.ValueObject

data class ProviderUserInfo(
    val nickName: String,
    val email: String,
) : ValueObject {
    override fun validate() {
        TODO("Not yet implemented")
    }
}
