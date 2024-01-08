package kr.co.jiniaslog.user.usecase

import kr.co.jiniaslog.user.domain.auth.AccessToken
import kr.co.jiniaslog.user.domain.auth.RefreshToken

interface IRefreshToken {
    fun handle(command: Command): Info

    data class Command(
        val refreshToken: RefreshToken,
    )

    data class Info(
        val accessToken: AccessToken,
        val refreshToken: RefreshToken,
    )
}
