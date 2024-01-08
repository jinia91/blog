package kr.co.jiniaslog.shared.security

import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration

class JwtTokenGeneratorTest {
    private val sut =
        JwtTokenGenerator(
            "12345678901234567890123456789012",
            Duration.ofMinutes(30),
            Duration.ofDays(30),
        )

    @Test
    fun `유효한 토큰은 검증에 통과한다`() {
        val token =
            sut.generateAccessToken(
                id = UserId(1),
                roles = setOf(Role.USER),
            ).value

        val result = sut.validateToken(token)
        Assertions.assertTrue(result)
    }
}
