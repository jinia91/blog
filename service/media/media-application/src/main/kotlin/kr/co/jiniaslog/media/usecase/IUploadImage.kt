package kr.co.jiniaslog.media.usecase

import kr.co.jiniaslog.media.domain.ImageUrl
import kr.co.jiniaslog.media.domain.RawImage

interface IUploadImage {
    fun uploadImage(command: Command): Info

    data class Command(val rawImage: RawImage)

    data class Info(val url: ImageUrl)
}
