package kr.co.jiniaslog.media.inbound.http

import kr.co.jiniaslog.media.usecase.UseCasesImageFacade
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/media")
@PreAuthorize("hasRole('ADMIN')")
class MediaController(
    private val useCaseImage: UseCasesImageFacade,
) {
    @PostMapping("/image")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun uploadImage(
        @ModelAttribute request: ImgUploadRequest,
    ): ImgUploadResponse {
        val command = request.toCommand()
        val info = useCaseImage.uploadImage(command)
        return ImgUploadResponse(info.url.value)
    }
}
