package kr.co.jiniaslog.blog.domain.user

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class UserVoFragmentsTests {
    @Test
    fun `유저 id는 0이 될 수 없다`() {
        shouldThrow<IllegalArgumentException> {
            UserId(0)
        }
    }
}
