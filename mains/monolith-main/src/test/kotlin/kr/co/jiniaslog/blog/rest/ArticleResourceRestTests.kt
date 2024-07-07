package kr.co.jiniaslog.blog.rest

import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.usecase.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.IStartToWriteNewArticle
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ArticleResourceRestTests : RestTestAbstractSkeleton() {
    @Test
    fun `인증된 사용자가 게시글을 시작하려하면 201을 반환한다`() {
        // given
        every {
            articleUseCasesFacade.handle(any(IStartToWriteNewArticle.Command::class))
        } returns IStartToWriteNewArticle.Info(ArticleId(1L))

        // when
        RestAssuredMockMvc.given()
            .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post("/api/v1/articles")
            // then
            .then()
            .statusCode(201)
    }

    @Test
    fun `인가 되지 않은 사용자가 게시글을 시작하려면 403을 반환한다`() {
        // given
        every {
            articleUseCasesFacade.handle(any(IStartToWriteNewArticle.Command::class))
        } returns IStartToWriteNewArticle.Info(ArticleId(1L))

        // when
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post("/api/v1/articles")
            // then
            .then()
            .statusCode(401)
    }

    @Test
    fun `인증되지 않은 사용자가 게시글을  시작하려하면 401을 반환한다`() {
        // given
        every {
            articleUseCasesFacade.handle(any(IStartToWriteNewArticle.Command::class))
        } returns IStartToWriteNewArticle.Info(ArticleId(1L))

        // when
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post("/api/v1/articles")
            // then
            .then()
            .statusCode(401)
    }

    @Test
    fun `정상적인 게시글 삭제 요청이 있으면 200을 반환한다`() {
        // given
        every { articleUseCasesFacade.handle(any(IDeleteArticle.Command::class)) } returns IDeleteArticle.Info()
        // when
        RestAssuredMockMvc.given()
            .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .delete("/api/v1/articles/1")
            // then
            .then()
            .statusCode(200)
    }
}
