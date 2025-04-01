package kr.co.jiniaslog.blog.rest

import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.AddTagToArticleRequest
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.UpdateArticleStatusRequest
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.queries.IGetExpectedStatusArticleById
import kr.co.jiniaslog.blog.queries.IGetSimpleArticles
import kr.co.jiniaslog.blog.usecase.article.ArticleStatusChangeFacade
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
            every {
                articleStatusChangeFacade.determineCommand(
                    Article.Status.DRAFT,
                    Article.Status.PUBLISHED,
                    ArticleId(1L)
                )
            } returns IPublishArticle.Command(ArticleId(1L))
            every { articleStatusChangeFacade.handle(any(ArticleStatusChangeFacade.Command::class)) } returns IPublishArticle.Info(
                ArticleId(1L)
            )
            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    UpdateArticleStatusRequest(
                        asIsStatus = Article.Status.DRAFT,
                        toBeStatus = Article.Status.PUBLISHED
                    )
                )
                .patch("/api/v1/articles/1")
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
            every {
                articleStatusChangeFacade.determineCommand(
                    Article.Status.PUBLISHED,
                    Article.Status.DRAFT,
                    ArticleId(1L)
                )
            } returns IPublishArticle.Command(ArticleId(1L))
            every {
                articleStatusChangeFacade.handle(any(ArticleStatusChangeFacade.Command::class))
            } returns IUnPublishArticle.Info(
                ArticleId(1L)
            )
            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    UpdateArticleStatusRequest(
                        asIsStatus = Article.Status.PUBLISHED,
                        toBeStatus = Article.Status.DRAFT
                    )
                )
                .patch("/api/v1/articles/1")
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
            every { articleStatusChangeFacade.handle(any(IDeleteArticle.Command::class)) } returns IDeleteArticle.Info(
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
            every {
                articleStatusChangeFacade.determineCommand(
                    Article.Status.DELETED,
                    Article.Status.DRAFT,
                    ArticleId(1L)
                )
            } returns IPublishArticle.Command(ArticleId(1L))
            every { articleStatusChangeFacade.handle(any(ArticleStatusChangeFacade.Command::class)) } returns IUnDeleteArticle.Info(
                ArticleId(1L)
            )

            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    UpdateArticleStatusRequest(
                        asIsStatus = Article.Status.DELETED,
                        toBeStatus = Article.Status.DRAFT
                    )
                )
                .patch("/api/v1/articles/1")
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
                .post("/api/v1/articles/1/tags")
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
            every {
                articleQueriesFacade.handle(any(IGetExpectedStatusArticleById.Query::class))
            } returns IGetExpectedStatusArticleById.Info(
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
                .get("/api/v1/articles/1?expectedStatus=PUBLISHED")
                // then
                .then()
                .statusCode(200)
        }

        @Test
        fun `게시된 게시물 간소조회 요청이 있으면 유저가 아니여도 200을 반환한다`() {
            // given
            every { articleQueriesFacade.handle(any(IGetSimpleArticles.Query::class)) } returns IGetSimpleArticles.Info(
                emptyList()
            )

            // when
            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/api/v1/articles/simple?cursor=1&limit=10&expectedStatus=PUBLISHED")
                // then
                .then()
                .statusCode(200)
        }

        @Test
        fun `DRAFT 간소조회 요청이 있으면 어드민의 경우 200을 반환한다`() {
            // given
            every { articleQueriesFacade.handle(any(IGetSimpleArticles.Query::class)) } returns IGetSimpleArticles.Info(
                emptyList()
            )

            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/api/v1/articles/simple?cursor=1&limit=10&expectedStatus=DRAFT")
                // then
                .then()
                .statusCode(200)
        }

        @Test
        fun `DRAFT 간소조회 요청이 있으면 일반 유저의 경우 403을 반환한다`() {
            // given
            every { articleQueriesFacade.handle(any(IGetSimpleArticles.Query::class)) } returns IGetSimpleArticles.Info(
                emptyList()
            )

            // when
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/api/v1/articles/simple?cursor=1&limit=10&expectedStatus=DRAFT")
                // then
                .then()
                .statusCode(403)
        }

        @Test
        fun `DRAFT 간소조회 요청이 있으면 유저가 아닐경우 401을 반환한다`() {
            // given
            every {
                articleQueriesFacade.handle(any(IGetExpectedStatusArticleById.Query::class))
            } returns IGetExpectedStatusArticleById.Info(
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
                .get("/api/v1/articles/simple?cursor=1&limit=10&expectedStatus=DRAFT")
                // then
                .then()
                .statusCode(401)
        }
    }
}
