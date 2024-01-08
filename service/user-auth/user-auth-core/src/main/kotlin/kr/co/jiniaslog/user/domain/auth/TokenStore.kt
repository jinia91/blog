package kr.co.jiniaslog.user.domain.auth

import kr.co.jiniaslog.user.domain.user.UserId

interface TokenStore {
    fun save(
        userId: UserId,
        accessToken: AccessToken,
        refreshToken: RefreshToken,
    )

    fun findByToken(userId: UserId): Pair<AccessToken, RefreshToken>?
}
