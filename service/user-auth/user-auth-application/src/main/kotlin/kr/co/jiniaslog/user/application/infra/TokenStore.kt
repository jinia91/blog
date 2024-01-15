package kr.co.jiniaslog.user.application.infra

import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.user.UserId

interface TokenStore {
    fun save(
        userId: UserId,
        accessToken: AccessToken,
        refreshToken: RefreshToken,
    )

    fun findByAuthTokens(userId: UserId): Triple<AccessToken, RefreshToken?, RefreshToken>?
}
