package kr.co.jiniaslog.blog.rest

import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.blog.usecase.category.ISyncCategories
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class CategoryResourceRestTests : RestTestAbstractSkeleton() {

    @Nested
    inner class `카테고리 싱크 테스트` {
        @Test
        fun `권한있는 인증된 사용자의 유효한 카테고리 요청이 오면 200을 반환한다`() {
            // given
            every {
                categoryUseCasesFacade.handle(any(ISyncCategories.Command::class))
            } returns ISyncCategories.Info()

            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(SyncCategoriesRequest(emptyList()))
                // when
                .put("/api/v1/categories")
                // then
                .then()
                .statusCode(200)
        }

        @Test
        fun `인가되지 않은 사용자의 카테고리 요청이 오면 403을 반환한다`() {
            // given
            every {
                categoryUseCasesFacade.handle(any(ISyncCategories.Command::class))
            } returns ISyncCategories.Info()

            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(SyncCategoriesRequest(emptyList()))
                // when
                .put("/api/v1/categories")
                // then
                .then()
                .statusCode(403)
        }

        @Test
        fun `인증되지 않은 사용자의 카테고리 요청이 오면 401을 반환한다`() {
            // given
            every {
                categoryUseCasesFacade.handle(any(ISyncCategories.Command::class))
            } returns ISyncCategories.Info()

            // when
            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(SyncCategoriesRequest(emptyList()))
                // when
                .put("/api/v1/categories")
                // then
                .then()
                .statusCode(401)
        }
    }
}
