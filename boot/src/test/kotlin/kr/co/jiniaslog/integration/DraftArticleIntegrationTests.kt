package kr.co.jiniaslog.integration

import kr.co.jiniaslog.config.TestContainerConfig
import io.restassured.RestAssured.given
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.adapter.persistence.CoreDB
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import org.apache.http.protocol.HTTP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class DraftArticleIntegrationTests : TestContainerConfig() {

    @Autowired
    private lateinit var draftArticleRepository: DraftArticleRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `temp Article 저장 테스트`() {
        // given:
        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")
            .body("""{
  "title": "ㅇㅇㅇㅇㅇ",
  "content": "",
  "thumbnailUrl": "",
  "writerId": 1,
  "categoryId": 0
                }""".trimIndent())

        // when:
        val response = given().spec(request)
            .`when`().post("/articles/temp")
            .then().extract()


        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        em.clear()
        val temp = draftArticleRepository.getById(DraftArticleId.getDefault())
        assertThat(temp).isNotNull
        assertThat(temp!!.id).isEqualTo(DraftArticleId.getDefault())
        assertThat(temp.title).isEqualTo("ㅇㅇㅇㅇㅇ")
    }

    @Test
    fun `temp article이 없을때 조회 테스트`() {
        // given:
        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")

        // when:
        val response = given().spec(request)
            .`when`().get("/articles/temp")
            .then().extract()

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
    }


    @Test
    fun `temp article이 있을때 조회 테스트`() {
        // given:
        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")

        draftArticleRepository.save(
            DraftArticle.Factory.from(
                writerId = UserId(value = 5626),
                title = "test",
                content = "test",
                thumbnailUrl = null,
                categoryId = CategoryId(4)
            )
        )

        em.clear()

        // when:
        val response = given().spec(request)
            .`when`().get("/articles/temp")
            .then().extract()

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        assertThat(response.jsonPath().getString("title")).isEqualTo("test")
    }
}