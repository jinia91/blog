package kr.co.jiniaslog.media.inbound.http.dto

import kr.co.jiniaslog.media.domain.RawImage
import kr.co.jiniaslog.media.usecase.image.IUploadImage
import org.springframework.web.multipart.MultipartFile

data class UploadImageRequest(val image: MultipartFile) {
    fun toCommand() = IUploadImage.Command(rawImage = RawImage(image.bytes))
}

data class UploadImageResponse(val url: String)
