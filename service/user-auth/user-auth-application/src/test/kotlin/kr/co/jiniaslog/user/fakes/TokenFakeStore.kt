package kr.co.jiniaslog.user.fakes

import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import kr.co.jiniaslog.user.application.infra.TokenStore
import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.user.UserId

@CustomComponent
class TokenFakeStore : TokenStore {
    private val tokens = mutableMapOf<UserId, Triple<AccessToken, RefreshToken, RefreshToken>>()

    override fun save(
        userId: UserId,
        accessToken: AccessToken,
        refreshToken: RefreshToken,
    ) {
        if (tokens.containsKey(userId)) {
            tokens[userId] = Triple(accessToken, tokens[userId]!!.third, refreshToken)
        } else {
            tokens[userId] = Triple(accessToken, refreshToken, refreshToken)
        }
    }

    override fun findByAuthTokens(userId: UserId): Triple<AccessToken, RefreshToken, RefreshToken>? {
        return tokens[userId]
    }

    fun tearDown() {
        tokens.clear()
    }
}
