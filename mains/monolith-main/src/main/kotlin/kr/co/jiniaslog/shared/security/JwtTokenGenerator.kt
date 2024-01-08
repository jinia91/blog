package kr.co.jiniaslog.shared.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
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
import java.util.Date

@Component
internal class JwtTokenGenerator(
    @Value("\${jwt.secret-key}")
    secretKey: String,
    @Value("\${jwt.token-valid-duration}")
    tokenValidDuration: Duration,
    @Value("\${jwt.refresh-token-valid-duration}")
    refreshTokenValidDuration: Duration,
) : TokenGenerator, TokenProvider {
    private val roleKey = "roles"
    private val tokenValidDuration = tokenValidDuration.toMillis()
    private val refreshTokenValidDuration = refreshTokenValidDuration.toMillis()
    private val key: Key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    override fun generateAccessToken(
        id: UserId,
        roles: Set<Role>,
    ): AccessToken {
        val now = Date()
        val token = generateToken(id, roles, now, tokenValidDuration)
        return AccessToken(token)
    }

    override fun generateRefreshToken(
        id: UserId,
        roles: Set<Role>,
    ): RefreshToken {
        val now = Date()
        val token = generateToken(id, roles, now, refreshTokenValidDuration)
        return RefreshToken(token)
    }

    override fun validateToken(token: String): Boolean {
        try {
            val claims: Jws<Claims> = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token)
            return !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            return false
        }
    }

    override fun getUserId(token: String): UserId {
        val claims =
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        return UserId(claims.subject.toLong())
    }

    override fun getRole(token: String): Set<Role> {
        val claims =
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        return claims[roleKey, List::class.java]
            .map { Role.valueOf(it as String) }
            .toSet()
    }

    private fun generateToken(
        id: UserId,
        roles: Set<Role>,
        now: Date,
        tokenValidDuration: Long,
    ): String =
        Jwts.builder()
            .claims()
            .subject(id.value.toString())
            .add(roleKey, roles)
            .issuedAt(now)
            .expiration(Date(now.time + tokenValidDuration))
            .and()
            .signWith(key)
            .compact()
}
