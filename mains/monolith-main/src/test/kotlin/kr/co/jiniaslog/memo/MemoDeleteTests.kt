package kr.co.jiniaslog.memo

import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.outbound.MemoRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.core.Neo4jClient

class MemoDeleteTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var memoRepository: MemoRepository

    @Autowired
    lateinit var neo4jClient: Neo4jClient

    @Test
    fun `유효한 메모 삭제 요청이 있다면 해당 메모는 삭제되어야 한다`() {
        // background
        val result =
            RestAssured
                .given()
                .body(
                    """
                    {
                     "authorId": 1
                    }
                    """.trimIndent(),
                )
                .contentType("application/json")
                .`when`()
                .post("/api/v1/memos")
                .then()
                .extract()
        val memoId = result.path<Long>("memoId")

        RestAssured
            .given()
            .contentType("application/json")
            .`when`()
            .delete("/api/v1/memos/$memoId")
            .then()
            .log().all()
            .statusCode(204)

        // 상태 검증
        memoRepository.findById(MemoId(memoId)) shouldBe null
    }
}
