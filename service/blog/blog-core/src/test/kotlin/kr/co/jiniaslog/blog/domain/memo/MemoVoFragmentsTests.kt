package kr.co.jiniaslog.blog.domain.memo

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class MemoVoFragmentsTests {
    @Test
    fun `메모 id는 0이 될 수 없다`() {
        // given
        // when, then
        shouldThrow<IllegalArgumentException> {
            MemoId(0)
        }
    }
}
