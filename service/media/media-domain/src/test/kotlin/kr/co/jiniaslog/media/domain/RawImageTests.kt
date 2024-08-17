package kr.co.jiniaslog.media.domain

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class RawImageTests {
    @Test
    fun `이미지가 아닌 이상한 파일로 RawImage 객체를 생성하려하면 예외가 발생한다`() {
        // given
        val bytes = "이상한 파일".toByteArray()

        // when
        shouldThrow<IllegalArgumentException> {
            RawImage(bytes)
        }
    }

    @Test
    fun `비어있는 RawImage 객체를 생성하려하면 예외가 발생한다`() {
        // given
        val bytes = ByteArray(0)

        // when
        shouldThrow<IllegalArgumentException> {
            RawImage(bytes)
        }
    }

    @Test
    fun `유효한 jpg 이미지파일로 RawImage 객체를 생성하면 예외가 발생하지 않는다`() {
        // given
        val bytes = this::class.java.getResourceAsStream("/sample.jpg")!!.readBytes()

        // when
        RawImage(bytes)

        // then
        assert(true)
    }
}
