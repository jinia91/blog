package kr.co.jiniaslog.blog.rest

import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.AddTagToArticleRequest
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.queries.IGetArticleById
import kr.co.jiniaslog.blog.usecase.article.IAddAnyTagInArticle
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import kr.co.jiniaslog.blog.usecase.article.IStartToWriteNewDraftArticle
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IUnPublishArticle
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class ArticleResourceRestTests : RestTestAbstractSkeleton() {
    @Nested
    inner class `게시글 시작 테스트` {

        @Test
        fun `인증된 사용자가 게시글을 시작하려하면 201을 반환한다`() {
            // given
            every {
                articleUseCasesFacade.handle(any(IStartToWriteNewDraftArticle.Command::class))
            } returns IStartToWriteNewDraftArticle.Info(ArticleId(1L))

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
                articleUseCasesFacade.handle(any(IStartToWriteNewDraftArticle.Command::class))
            } returns IStartToWriteNewDraftArticle.Info(ArticleId(1L))

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
                articleUseCasesFacade.handle(any(IStartToWriteNewDraftArticle.Command::class))
            } returns IStartToWriteNewDraftArticle.Info(ArticleId(1L))

            // when
            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/api/v1/articles")
                // then
                .then()
                .statusCode(401)
        }
    }

    @Nested
    inner class `게시글 게시 테스트` {
        @Test
        fun `어드민 사용자의 유효한 게시글 게시 요청이 있으면 200을 반환한다`() {
            // given
            every { articleUseCasesFacade.handle(any(IPublishArticle.Command::class)) } returns IPublishArticle.Info(
                ArticleId(1L)
            )
            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put("/api/v1/articles/1/publish")
                // then
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `게시글 내리기 테스트` {
        @Test
        fun `어드민 사용자의 유효한 게시글 내리기 요청이 있으면 200을 반환한다`() {
            // given
            every { articleUseCasesFacade.handle(any(IUnPublishArticle.Command::class)) } returns IUnPublishArticle.Info(
                ArticleId(1L)
            )
            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put("/api/v1/articles/1/draft")
                // then
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `게시글 삭제 테스트` {
        @Test
        fun `정상적인 게시글 삭제 요청이 있으면 200을 반환한다`() {
            // given
            every { articleUseCasesFacade.handle(any(IDeleteArticle.Command::class)) } returns IDeleteArticle.Info(
                ArticleId(1L)
            )
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

    @Nested
    inner class `게시글 삭제 복구 테스트` {
        @Test
        fun `삭제된 게시글을 성공적으로 복구하면 200을 반환한다`() {
            // given
            every { articleUseCasesFacade.handle(any(IUnDeleteArticle.Command::class)) } returns IUnDeleteArticle.Info(
                ArticleId(1L)
            )

            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put("/api/v1/articles/1/undelete")
                // then
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `게시글 태그 추가 테스트` {
        @Test
        fun `유효한 태그 추가 요청이 있으면 200을 반환한다`() {
            // given
            every { articleUseCasesFacade.handle(any(IAddAnyTagInArticle.Command::class)) } returns IAddAnyTagInArticle.Info(
                ArticleId(1L)
            )

            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(AddTagToArticleRequest("tag"))
                // when
                .put("/api/v1/articles/1/tag")
                // then
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `게시글 조회 테스트` {
        @Test
        fun `게시글 조회 요청이 있으면 유저가 아니여도 200을 반환한다`() {
            // given
            every { articleQueriesFacade.handle(any(IGetArticleById.Query::class)) } returns IGetArticleById.Info(
                id = ArticleId(1L),
                title = "title",
                content = "content",
                thumbnailUrl = "thumbnailUrl",
                tags = emptyMap(),
                createdAt = LocalDateTime.now(),
                isPublished = false
            )

            // when
            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/api/v1/articles/1")
                // then
                .then()
                .statusCode(200)
        }
    }
}
