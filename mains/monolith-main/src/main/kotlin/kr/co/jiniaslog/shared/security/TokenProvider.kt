package kr.co.jiniaslog.shared.security

import kr.co.jiniaslog.user.domain.auth.AccessToken
import kr.co.jiniaslog.user.domain.auth.RefreshToken
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId

interface TokenProvider {
    fun generateAccessToken(
        id: UserId,
        roles: Set<Role>,
    ): AccessToken

    fun generateRefreshToken(
        id: UserId,
        roles: Set<Role>,
    ): RefreshToken

    fun validateToken(token: String): Boolean

    fun getUserId(token: String): UserId

    fun getRole(token: String): Set<Role>
}
