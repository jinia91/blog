package kr.co.jiniaslog.media.inbound.http

import kr.co.jiniaslog.media.usecase.ImageUseCasesFacade
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/media/image")
@PreAuthorize("hasRole('ADMIN')")
class ImageResources(
    private val imageUseCases: ImageUseCasesFacade,
) {
    @PostMapping()
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun uploadImage(@ModelAttribute request: ImgUploadRequest): ResponseEntity<ImgUploadResponse> {
        val command = request.toCommand()
        val info = imageUseCases.uploadImage(command)
        return ResponseEntity.ok(ImgUploadResponse(info.url.value))
    }
}
