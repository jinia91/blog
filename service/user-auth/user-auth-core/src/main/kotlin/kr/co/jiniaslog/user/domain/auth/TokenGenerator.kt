package kr.co.jiniaslog.user.domain.auth

import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId

interface TokenGenerator {
    fun generateAccessToken(
        id: UserId,
        role: Role,
    ): AccessToken

    fun generateRefreshToken(
        id: UserId,
        role: Role,
    ): RefreshToken
}
