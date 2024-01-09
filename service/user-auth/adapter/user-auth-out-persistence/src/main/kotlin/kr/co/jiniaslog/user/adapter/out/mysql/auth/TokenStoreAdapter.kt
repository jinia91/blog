package kr.co.jiniaslog.user.adapter.out.mysql.auth

import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kr.co.jiniaslog.user.domain.auth.AccessToken
import kr.co.jiniaslog.user.domain.auth.RefreshToken
import kr.co.jiniaslog.user.domain.auth.TokenStore
import kr.co.jiniaslog.user.domain.user.UserId
import kotlin.jvm.optionals.getOrNull

@PersistenceAdapter
class TokenStoreAdapter(
    private val tokenRepository: TokenRepository,
) : TokenStore {
    override fun save(
        userId: UserId,
        accessToken: AccessToken,
        refreshToken: RefreshToken,
    ) {
        val token = tokenRepository.findById(userId.value).getOrNull()
        if (token == null) {
            tokenRepository.save(TokenJpaEntity(userId.value, accessToken.value, refreshToken.value, null, null))
        } else {
            token.accessToken = accessToken.value
            token.refreshToken = refreshToken.value
            tokenRepository.save(token)
        }
    }

    override fun findByToken(userId: UserId): Pair<AccessToken, RefreshToken>? {
        return tokenRepository.findById(userId.value).getOrNull()?.let {
            Pair(AccessToken(it.accessToken), RefreshToken(it.refreshToken))
        }
    }
}
