package kr.co.jiniaslog.media.inbound.http

import kr.co.jiniaslog.media.inbound.http.dto.UploadImageRequest
import kr.co.jiniaslog.media.inbound.http.dto.UploadImageResponse
import kr.co.jiniaslog.media.usecase.image.ImageUseCasesFacade
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/media/image")
@PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
class ImageResources(
    private val imageUseCases: ImageUseCasesFacade,
) {
    @PostMapping()
    fun uploadImage(@ModelAttribute request: UploadImageRequest): ResponseEntity<UploadImageResponse> {
        val command = request.toCommand()
        val info = imageUseCases.uploadImage(command)
        return ResponseEntity.created(URI.create(info.url.value))
            .body(UploadImageResponse(info.url.value))
    }
}
