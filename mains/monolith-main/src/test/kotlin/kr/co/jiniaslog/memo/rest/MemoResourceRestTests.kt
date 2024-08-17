package kr.co.jiniaslog.memo.rest

import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.AddParentFolderRequest
import kr.co.jiniaslog.memo.adapter.inbound.http.dto.InitMemoRequest
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.usecase.IDeleteMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class MemoResourceRestTests : RestTestAbstractSkeleton() {

    @Nested
    inner class `메모 리소스 인증 인가` {
        @Test
        fun `인증이 없는 사용자가 메모 생성 요청시 401을 받는다`() {
            // given
            every { memoService.handle(any(IInitMemo.Command::class)) } returns IInitMemo.Info(MemoId(1L))

            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(InitMemoRequest(null))
                // when
                .post("/api/v1/memos")
                // then
                .then()
                .statusCode(401)
        }

        @Test
        fun `인증이 없는 사용자가 스키마가 틀린 요청시 401을 받는다`() {
            // given
            every { memoService.handle(any(IInitMemo.Command::class)) } returns IInitMemo.Info(MemoId(1L))

            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("test")
                // when
                .post("/api/v1/memos")
                // then
                .then()
                .statusCode(401)
        }
    }

    @Nested
    inner class `메모 생성` {
        @Test
        fun `유효한 메모 생성 요청시 201을 받는다`() {
            // given
            every { memoService.handle(any(IInitMemo.Command::class)) } returns IInitMemo.Info(MemoId(1L))

            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(InitMemoRequest(null))
                // when
                .post("/api/v1/memos")
                // then
                .then()
                .statusCode(201)
        }
    }

    @Nested
    inner class `메모 삭제` {
        @Test
        fun `유효한 메모 삭제 요청시 204를 받는다`() {
            // given
            every { memoService.handle(any(IDeleteMemo.Command::class)) } returns IDeleteMemo.Info()

            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                // when
                .delete("/api/v1/memos/1")
                // then
                .then()
                .statusCode(204)
        }
    }

    @Nested
    inner class `메모에 부모 폴더 추가` {
        @Test
        fun `유효한 메모에 부모 폴더 추가 요청시 200을 받는다`() {
            // given
            every { memoService.handle(any(IMakeRelationShipFolderAndMemo.Command::class)) } returns
                IMakeRelationShipFolderAndMemo.Info(MemoId(1L), FolderId(10L))

            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(AddParentFolderRequest(10))
                // when
                .put("/api/v1/memos/1/parent")
                // then
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `메모 조회` {
        @Test
        fun `유효한 아이디로 조회시 메모는 조회된다`() {
            // given
            every { memoQueries.handle(any(IGetMemoById.Query::class)) } returns
                IGetMemoById.Info(memoId = MemoId(1L), title = MemoTitle("TITLE"), content = MemoContent("content"), references = emptySet())

            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                // when
                .get("/api/v1/memos/1")
                // then
                .then()
                .statusCode(200)
        }

        @Test
        fun `유효한 메모 아이디로 추천 메모를 조회하면 조회된다`() {
            // given
            every { memoQueries.handle(any(IRecommendRelatedMemo.Query::class)) } returns
                IRecommendRelatedMemo.Info(
                    relatedMemoCandidates = listOf(
                        Triple(MemoId(1L), MemoTitle("TITLE"), MemoContent("content")),
                        Triple(MemoId(2L), MemoTitle("TITLE"), MemoContent("content")),
                    ),
                )

            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                // when
                .get("/api/v1/memos/1/recommended?keyword=keyword")
                // then
                .then()
                .statusCode(200)
        }

        @Test
        fun `유효한 메모가 참조하는 메모를 조회하면 조회된다`() {
            // given
            every { memoQueries.handle(any(IGetAllReferencesByMemo.Query::class)) } returns
                IGetAllReferencesByMemo.Info(
                    references = setOf(
                        IGetAllReferencesByMemo.ReferenceInfo(MemoId(2L), MemoTitle("TITLE")),
                        IGetAllReferencesByMemo.ReferenceInfo(MemoId(3L), MemoTitle("TITLE")),
                    ),
                )

            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                // when
                .get("/api/v1/memos/1/references")
                // then
                .then()
                .statusCode(200)
        }

        @Test
        fun `유효한 메모가 참조된 메모를 조회하면 조회된다`() {
            // given
            every { memoQueries.handle(any(IGetAllReferencedByMemo.Query::class)) } returns
                IGetAllReferencedByMemo.Info(
                    referenceds = setOf(
                        IGetAllReferencedByMemo.ReferencedInfo(MemoId(1L), MemoTitle("TITLE")),
                        IGetAllReferencedByMemo.ReferencedInfo(MemoId(1L), MemoTitle("TITLE")),
                    ),
                )

            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                // when
                .get("/api/v1/memos/3/referenced")
                // then
                .then()
                .statusCode(200)
        }
    }
}
