package kr.co.jiniaslog.media.rest

import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.media.domain.ImageUrl
import kr.co.jiniaslog.media.usecase.IUploadImage
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ImageResourceRestTests : RestTestAbstractSkeleton() {

    private val sampleImageFile = this::class.java.getResourceAsStream("/sample.jpg")!!.readBytes()

    @Test
    fun `인증되지 않은 사용자가 이미지를 업로드하려하면 401을 반환한다`() {
        // given
        every { imageService.uploadImage(any()) } returns IUploadImage.Info(ImageUrl("http://test.com/sample.jpg"))

        RestAssuredMockMvc.given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .multiPart("image", "test.jpg", sampleImageFile)
            // when
            .post("/api/v1/media/image")
            // then
            .then()
            .statusCode(401)
    }

    @Test
    fun `올바르지 않은 스키마 요청이 들어와도 인증되지 않은 사용자라면 401을 반환한다`() {
        // given
        every { imageService.uploadImage(any()) } returns IUploadImage.Info(ImageUrl("http://test.com/sample.jpg"))

        RestAssuredMockMvc.given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .multiPart("image", "test.jpg", "".toByteArray())
            // when
            .post("/api/v1/media/image")
            // then
            .then()
            .statusCode(401)
    }

    @Test
    fun `권한이 없는 사용자가 이미지를 업로드하려하면 403을 반환한다`() {
        // given
        every { imageService.uploadImage(any()) } returns IUploadImage.Info(ImageUrl("http://test.com/sample.jpg"))

        RestAssuredMockMvc.given()
            .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .multiPart("image", "test.jpg", sampleImageFile)
            // when
            .post("/api/v1/media/image")
            // then
            .then()
            .statusCode(403)
    }

    @Test
    fun `권한이 있고 인증된 사용자가 이미지를 업로드하면 200을 반환한다`() {
        // given
        every { imageService.uploadImage(any()) } returns IUploadImage.Info(ImageUrl("http://test.com/sample.jpg"))

        RestAssuredMockMvc.given()
            .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .multiPart("image", "test.jpg", sampleImageFile)
            // when
            .post("/api/v1/media/image")
            // then
            .then()
            .statusCode(200)
    }

    @Test
    fun `권한이 있고 인증된사용자가 잘못된 이미지를 업로드하려하면 400을 반환한다`() {
        // given
        every { imageService.uploadImage(any()) } returns IUploadImage.Info(ImageUrl("http://test.com/sample.jpg"))

        RestAssuredMockMvc.given()
            .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .multiPart("image", "test.jpg", "".toByteArray())
            // when
            .post("/api/v1/media/image")
            // then
            .then()
            .statusCode(400)
    }
}
