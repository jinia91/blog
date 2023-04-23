package kr.co.jiniaslog.integration

import kr.co.jiniaslog.config.TestContainerConfig
import io.restassured.RestAssured.given
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.adapter.http.article.ArticlePostResponse
import kr.co.jiniaslog.blogcore.adapter.persistence.CoreDB
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import org.apache.http.protocol.HTTP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class ArticleIntegrationTests : TestContainerConfig() {

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `Draft Article 저장 테스트`() {
        // given:
        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")
            .body("""{
  "writerId": 73,
  "title": "test_d0ce030f400d",
  "content": "test_9a1d30d00fb4",
  "thumbnailUrl": "test_d46086707282",
  "categoryId": 41,
  "tags": [68]
}""".trimIndent())

        // when:
        val response = given().spec(request)
            .`when`().post("/articles/draft")
            .then().extract()

        val deSerializedResponse = response.body().jsonPath().getObject("", ArticlePostResponse::class.java)

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        em.clear()
        val foundOne = articleRepository.findById(ArticleId(deSerializedResponse.articleId))
        assertThat(foundOne).isNotNull
        assertThat(foundOne!!.id).isEqualTo(ArticleId(deSerializedResponse.articleId))
        assertThat(foundOne.title).isEqualTo("test_d0ce030f400d")
    }

//    @Test
//    fun `temp article이 없을때 조회 테스트`() {
//        // given:
//        val request = given()
//            .header(HTTP.CONTENT_TYPE, "application/json")
//
//        // when:
//        val response = given().spec(request)
//            .`when`().get("/articles/temp")
//            .then().extract()
//
//        // then:
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
//    }
//
//
//    @Test
//    fun `temp article이 있을때 조회 테스트`() {
//        // given:
//        val request = given()
//            .header(HTTP.CONTENT_TYPE, "application/json")
//
//        articleRepository.save(
//            TempArticle.Factory.from(
//                writerId = UserId(value = 5626),
//                title = "test",
//                content = "test",
//                thumbnailUrl = null,
//                categoryId = CategoryId(4)
//            )
//        )
//
//        em.clear()
//
//        // when:
//        val response = given().spec(request)
//            .`when`().get("/articles/temp")
//            .then().extract()
//
//        // then:
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
//        assertThat(response.jsonPath().getString("title")).isEqualTo("test")
//    }
}