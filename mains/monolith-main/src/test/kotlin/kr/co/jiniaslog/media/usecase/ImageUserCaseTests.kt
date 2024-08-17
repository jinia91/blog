package kr.co.jiniaslog.media.usecase

import io.kotest.matchers.shouldNotBe
import io.mockk.every
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.media.domain.ImageUrl
import kr.co.jiniaslog.media.domain.RawImage
import kr.co.jiniaslog.media.outbound.ImageUploader
import kr.co.jiniaslog.media.usecase.image.IUploadImage
import kr.co.jiniaslog.media.usecase.image.ImageUseCasesFacade
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ImageUserCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    private lateinit var sut: ImageUseCasesFacade

    @Autowired
    private lateinit var mockImageUploader: ImageUploader

    @Test
    fun `이미지를 업로드하면 이미지 URL을 반환한다`() {
        // given
        val imageFile = this::class.java.getResourceAsStream("/sample.jpg")!!.readBytes()
        val command = IUploadImage.Command(RawImage(imageFile))
        val dummyUrl = "http://test.com/sample.jpg"
        every { mockImageUploader.uploadImage(any()) } returns ImageUrl(dummyUrl)

        // when
        val info = sut.uploadImage(command)

        // then
        info.url shouldNotBe dummyUrl
    }
}
