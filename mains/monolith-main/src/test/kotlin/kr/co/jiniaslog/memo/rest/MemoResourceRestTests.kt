package kr.co.jiniaslog.memo.rest

import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import kr.co.jiniaslog.RestTestAbstractSkeleton
import kr.co.jiniaslog.memo.adapter.inbound.http.InitMemoRequest
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.user.application.security.PreAuthFilter
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class MemoResourceRestTests : RestTestAbstractSkeleton() {

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
