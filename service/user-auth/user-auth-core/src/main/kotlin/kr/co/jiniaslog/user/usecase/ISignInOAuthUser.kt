package kr.co.jiniaslog.user.usecase

import kr.co.jiniaslog.user.domain.auth.AccessToken
import kr.co.jiniaslog.user.domain.auth.AuthorizationCode
import kr.co.jiniaslog.user.domain.auth.Provider
import kr.co.jiniaslog.user.domain.auth.RefreshToken
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName

interface ISignInOAuthUser {
    fun handle(command: Command): Info

    data class Command(
        val provider: Provider,
        val code: AuthorizationCode,
    )

    data class Info(
        val accessToken: AccessToken,
        val refreshToken: RefreshToken,
        val nickName: NickName,
        val email: Email,
    )
}
