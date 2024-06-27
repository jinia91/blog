package kr.co.jiniaslog.media.domain

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import org.apache.commons.imaging.ImageFormats
import org.apache.commons.imaging.Imaging

@JvmInline
value class RawImage(val value: ByteArray) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotEmpty()) { "이미지 원본은 비어있을 수 없습니다." }
        require(isValidImage()) { "유효하지 않은 이미지 형식입니다." }
    }

    private fun isValidImage(): Boolean {
        return try {
            Imaging.guessFormat(value) != ImageFormats.UNKNOWN
        } catch (e: Exception) {
            false
        }
    }
}

@JvmInline
value class ImageId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) {
            "이미지 ID는 0보다 커야 합니다."
        }
    }
}

@JvmInline
value class ImageUrl(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) {
            "이미지 URL은 공백일 수 없습니다."
        }
    }
}
