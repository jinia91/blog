package kr.co.jiniaslog.blog.domain.tag

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class TagVoFragmentsTests {

    @Test
    fun `태그 이름이 20자를 넘으면 생성할 수 없다`() {
        // given
        // when, then
        shouldThrow<IllegalArgumentException> {
            TagName("a".repeat(21))
        }
    }

    @Test
    fun `태그 이름은 비어있을 수 없다`() {
        // given
        // when, then
        shouldThrow<IllegalArgumentException> {
            TagName("")
        }
    }

    @Test
    fun `태그 id는 0이 될 수 없다`() {
        // given
        // when, then
        shouldThrow<IllegalArgumentException> {
            TagId(0)
        }
    }
}
