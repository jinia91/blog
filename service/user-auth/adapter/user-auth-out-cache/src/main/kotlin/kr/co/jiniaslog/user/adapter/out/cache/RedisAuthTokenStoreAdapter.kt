package kr.co.jiniaslog.user.adapter.out.cache

import kr.co.jiniaslog.shared.core.annotation.NoArgConstructor
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kr.co.jiniaslog.user.application.infra.AuthTokenInfo
import kr.co.jiniaslog.user.application.infra.TokenStore
import kr.co.jiniaslog.user.domain.auth.token.AccessToken
import kr.co.jiniaslog.user.domain.auth.token.RefreshToken
import kr.co.jiniaslog.user.domain.user.UserId
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit

@NoArgConstructor
data class AuthTokenRedisDataHolder(
    val accessToken: String,
    val refreshToken: String,
)

@PersistenceAdapter
internal class RedisAuthTokenStoreAdapter(
    private val redisTemplate: RedisTemplate<String, Any>
) : TokenStore {
    override fun save(
        userId: UserId,
        accessToken: AccessToken,
        refreshToken: RefreshToken,
    ) {
        val key = userId.value.toString()
        val existingTokens = redisTemplate.opsForValue().get(key) as? AuthTokenRedisDataHolder
        if (existingTokens != null) {
            val tempKey = "temp:$key"
            redisTemplate.opsForValue().set(tempKey, existingTokens.refreshToken, 3, TimeUnit.SECONDS)
        }
        redisTemplate.opsForValue().set(key, AuthTokenRedisDataHolder(accessToken.value, refreshToken.value))
    }

    override fun findByUserId(userId: UserId): AuthTokenInfo? {
        val key = userId.value.toString()
        val authTokens = redisTemplate.opsForValue().get(key) as? AuthTokenRedisDataHolder
        val tempKey = "temp:$key"
        val tempToken = redisTemplate.opsForValue().get(tempKey) as? String

        return if (authTokens != null) {
            AuthTokenInfo(
                AccessToken(authTokens.accessToken),
                tempToken?.let {
                    RefreshToken(it)
                },
                RefreshToken(authTokens.refreshToken)
            )
        } else {
            null
        }
    }

    override fun delete(userId: UserId) {
        val key = userId.value.toString()
        val tempKey = "temp:$key"
        redisTemplate.delete(key)
        redisTemplate.delete(tempKey)
    }
}
