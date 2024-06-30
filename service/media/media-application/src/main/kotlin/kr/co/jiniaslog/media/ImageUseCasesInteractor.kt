package kr.co.jiniaslog.media

import kr.co.jiniaslog.media.domain.ImageFile
import kr.co.jiniaslog.media.domain.ImageId
import kr.co.jiniaslog.media.outbound.ImageUploader
import kr.co.jiniaslog.media.usecase.IUploadImage
import kr.co.jiniaslog.media.usecase.ImageUseCasesFacade
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.IdUtils

@UseCaseInteractor
internal class ImageUseCasesInteractor(
    private val imageUploader: ImageUploader,
) : ImageUseCasesFacade {
    override fun uploadImage(command: IUploadImage.Command): IUploadImage.Info {
        val imageFile = command.toImageFile()
        val url = imageUploader.uploadImage(imageFile)
        return IUploadImage.Info(url)
    }

    private fun IUploadImage.Command.toImageFile(): ImageFile {
        return ImageFile(
            id = ImageId(IdUtils.generate()),
            rawImage = this.rawImage,
        )
    }
}
