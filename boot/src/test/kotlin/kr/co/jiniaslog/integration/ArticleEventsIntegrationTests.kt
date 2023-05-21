package kr.co.jiniaslog.integration

import kr.co.jiniaslog.config.TestContainerConfig
import io.restassured.RestAssured.given
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.adapter.http.article.ArticlePostApiResponse
import kr.co.jiniaslog.blogcore.adapter.persistence.CoreDB
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import org.apache.http.protocol.HTTP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class ArticleEventsIntegrationTests : TestContainerConfig() {

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var draftArticleRepository: DraftArticleRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `post Article 저장시 draft Article 삭제 테스트`() {
        // given:
        val draftArticleId = DraftArticleId(5)
        draftArticleRepository.save(
            DraftArticle.Factory.newOne(
                id = draftArticleId,
                writerId = UserId(1),
                title = "test_d0ce030f400d",
                content = "test_d0ce030f400d",
                thumbnailUrl = "https://example.com/thumbnail.png",
            )
        )
        em.clear()

        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")
            .body(
                """{
    "writerId": 1,
    "title": "test_d0ce030f400d",
    "content": "test_d0ce030f400d",
    "thumbnailUrl": "https://example.com/thumbnail.png",
    "categoryId": 1,
    "tags": [
      1,
      2,
      3
    ],
    "draftArticleId": ${draftArticleId.value}
    }""".trimIndent()
            )

        // when:
        val response = given().spec(request)
            .`when`().post("api/articles")
            .then().extract()

        val deSerializedResponse = response.body().jsonPath().getObject("", ArticlePostApiResponse::class.java)

        Thread.sleep(500)

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        em.clear()
        val foundArticle = articleRepository.findById(ArticleId(deSerializedResponse.articleId))
        val foundDraftArticle = draftArticleRepository.findById(draftArticleId)
        assertThat(foundArticle).isNotNull
        assertThat(foundArticle!!.id).isEqualTo(ArticleId(deSerializedResponse.articleId))
        assertThat(foundArticle.title).isEqualTo("test_d0ce030f400d")
        assertThat(foundDraftArticle).isNull()
    }
}