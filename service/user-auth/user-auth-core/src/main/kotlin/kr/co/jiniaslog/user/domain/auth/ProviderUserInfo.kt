package kr.co.jiniaslog.user.domain.auth

import kr.co.jiniaslog.shared.core.domain.ValueObject
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName

data class ProviderUserInfo(
    val nickName: NickName,
    val email: Email,
    val picture: Url?,
) : ValueObject {
    override fun validate() {
        TODO("Not yet implemented")
    }
}
