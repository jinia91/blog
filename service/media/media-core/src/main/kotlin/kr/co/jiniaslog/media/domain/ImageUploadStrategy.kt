package kr.co.jiniaslog.media.domain

interface ImageUploadStrategy {
    fun uploadImage(imageFile: ImageFile): ImageUrl
}
