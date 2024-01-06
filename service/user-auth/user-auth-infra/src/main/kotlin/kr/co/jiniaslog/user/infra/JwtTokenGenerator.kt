package kr.co.jiniaslog.user.infra

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import kr.co.jiniaslog.user.domain.auth.AccessToken
import kr.co.jiniaslog.user.domain.auth.RefreshToken
import kr.co.jiniaslog.user.domain.auth.TokenGenerator
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Duration
import java.util.Base64
import java.util.Date

@Component
internal class JwtTokenGenerator(
    @Value("\${jwt.secret-key}")
    secretKey: String,
    @Value("\${jwt.token-valid-duration}")
    tokenValidDuration: Duration,
    @Value("\${jwt.refresh-token-valid-duration}")
    refreshTokenValidDuration: Duration,
) : TokenGenerator {
    private val roleKey = "roles"
    private val secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    private val tokenValidDuration = tokenValidDuration.toMillis()
    private val refreshTokenValidDuration = refreshTokenValidDuration.toMillis()
    private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    override fun generateAccessToken(
        id: UserId,
        role: Role,
    ): AccessToken {
        val now = Date()
        val token = generateToken(id, role, now, tokenValidDuration)
        return AccessToken(token)
    }

    override fun generateRefreshToken(
        id: UserId,
        role: Role,
    ): RefreshToken {
        val now = Date()
        val token = generateToken(id, role, now, refreshTokenValidDuration)
        return RefreshToken(token)
    }

    private fun generateToken(
        id: UserId,
        role: Role,
        now: Date,
        tokenValidDuration: Long,
    ): String =
        Jwts.builder()
            .claims()
            .subject(id.value.toString())
            .add(roleKey, role)
            .issuedAt(now)
            .expiration(Date(now.time + tokenValidDuration))
            .and()
            .signWith(key)
            .compact()
}
