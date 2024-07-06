package kr.co.jiniaslog.memo.rest

import io.mockk.every
import io.mockk.mockk
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.memo.adapter.inbound.http.ChangeFolderNameRequest
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchy
import kr.co.jiniaslog.memo.usecase.IChangeFolderName
import kr.co.jiniaslog.memo.usecase.ICreateNewFolder
import kr.co.jiniaslog.memo.usecase.IDeleteFoldersRecursively
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndFolder
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class FolderResourceRestTests : RestTestAbstractSkeleton() {
    @Nested
    inner class `폴더 리소스 인증 인가` {
        @Test
        fun `인증 되지 않은 사용자가 폴더 생성 요청시 401을 받는다`() {
            // given
            every { folderService.handle(any(ICreateNewFolder.Command::class)) } returns ICreateNewFolder.Info(
                FolderId(
                    1L
                ),
                FolderName("name")
            )

            // when, then
            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .post("/api/v1/folders")
                .then()
                .statusCode(401)
        }

        @Test
        fun `인가가 되지 않은 사용자가 폴더 생성 요청시 403을 받는다`() {
            // given
            every { folderService.handle(any(ICreateNewFolder.Command::class)) } returns ICreateNewFolder.Info(
                FolderId(
                    1L
                ),
                FolderName("name")
            )

            // when, then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .post("/api/v1/folders")
                .then()
                .statusCode(403)
        }

        @Test
        fun `인증된 사용자가 폴더 생성 요청시 201을 받는다`() {
            // given
            every { folderService.handle(any(ICreateNewFolder.Command::class)) } returns ICreateNewFolder.Info(
                FolderId(
                    1L
                ),
                FolderName("name")
            )

            // when, then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .post("/api/v1/folders")
                .then()
                .statusCode(201)
        }

        @Test
        fun `인증되지 않은 사용자가 잘못된 요청 스키마로 생성 요청시 401을 받는다`() {
            // given
            every { folderService.handle(any(ICreateNewFolder.Command::class)) } returns ICreateNewFolder.Info(
                FolderId(
                    1L
                ),
                FolderName("name")
            )

            // when, then
            RestAssuredMockMvc.given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("test")
                .post("/api/v1/folders")
                .then()
                .statusCode(401)
        }
    }

    @Nested
    inner class `폴더 생성` {
        @Test
        fun `유효한 폴더 생성 요청시 200을 받는다`() {
            // given
            every { folderService.handle(any(ICreateNewFolder.Command::class)) } returns ICreateNewFolder.Info(
                FolderId(
                    1L
                ),
                FolderName("name")
            )

            // when, then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .post("/api/v1/folders")
                .then()
                .statusCode(201)
        }
    }

    @Nested
    inner class `폴더 이름 변경` {
        @Test
        fun `유효한 폴더 이름 변경 요청시 200을 받는다`() {
            // given
            every { folderService.handle(any(IChangeFolderName.Command::class)) } returns IChangeFolderName.Info(
                FolderId(
                    1L
                )
            )

            // when, then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ChangeFolderNameRequest(1L, "name"))
                .put("/api/v1/folders/1/name")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `폴더 삭제` {
        @Test
        fun `유효한 폴더 삭제 요청시 200을 받는다`() {
            // given
            every { folderService.handle(any(IDeleteFoldersRecursively.Command::class)) } returns IDeleteFoldersRecursively.Info(
                FolderId(
                    1L
                )
            )

            // when, then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .delete("/api/v1/folders/1")
                .then()
                .statusCode(200)
                .body("folderId", equalTo(1))
        }
    }

    @Nested
    inner class `폴더간 관계 설정` {
        @Test
        fun `유효한 폴더간 관계 설정 요청시 200을 받는다`() {
            // given
            every {
                folderService.handle(any(IMakeRelationShipFolderAndFolder.Command::class))
            } returns IMakeRelationShipFolderAndFolder.Info(
                FolderId(1L), FolderId(2L)
            )

            // when, then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .put("/api/v1/folders/1/parent/2")
                .then()
                .statusCode(200)
        }
    }

    @Nested
    inner class `폴더 메모 조회` {
        @Test
        fun `유효한 폴더 메모 조회 요청시 200을 받는다`() {
            // given
            every { folderQueries.handle(any(IGetFoldersAllInHierirchy.Query::class)) } returns mockk(relaxed = true)

            // when, then
            RestAssuredMockMvc.given()
                .cookies(PreAuthFilter.ACCESS_TOKEN_HEADER, getTestAdminUserToken())
                .get("/api/v1/folders")
                .then()
                .statusCode(200)
        }
    }
}
