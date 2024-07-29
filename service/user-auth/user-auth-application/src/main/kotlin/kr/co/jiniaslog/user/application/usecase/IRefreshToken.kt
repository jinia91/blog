package kr.co.jiniaslog.user.application.usecase

import kr.co.jiniaslog.shared.core.domain.vo.Url
import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.Role

interface IRefreshToken {
    fun handle(command: Command): Info

    data class Command(
        val refreshToken: RefreshToken,
    )

    data class Info(
        val accessToken: AccessToken,
        val refreshToken: RefreshToken,
        val nickName: NickName,
        val email: Email,
        val roles: Set<Role>,
        val picUrl: Url?,
    )
}
