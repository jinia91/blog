package kr.co.jiniaslog.media.domain

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class ImageFragmentTests {
    @Test
    fun `이미지 url은 공백일 수 없다`() {
        // given
        val url = ""

        // when, then
        shouldThrow<IllegalArgumentException> { ImageUrl(url) }
    }

    @Test
    fun `이미지 id는 0보다 커야 한다`() {
        // given
        val id = -1L

        // when, then
        shouldThrow<IllegalArgumentException> { ImageId(id) }
    }
}
