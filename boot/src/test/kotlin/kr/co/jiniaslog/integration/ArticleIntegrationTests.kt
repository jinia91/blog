package kr.co.jiniaslog.integration

import kr.co.jiniaslog.config.TestContainerConfig
import io.restassured.RestAssured.given
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.adapter.http.article.ArticleEditApiResponse
import kr.co.jiniaslog.blogcore.adapter.http.article.ArticlePostApiResponse
import kr.co.jiniaslog.blogcore.adapter.persistence.CoreDB
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import org.apache.http.protocol.HTTP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class ArticleIntegrationTests : TestContainerConfig() {

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var draftArticleRepository: DraftArticleRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `post Article 저장 테스트`() {
        // given:
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
  ]
  }""".trimIndent()
            )

        // when:
        val response = given().spec(request)
            .`when`().post("api/articles")
            .then().extract()

        val deSerializedResponse = response.body().jsonPath().getObject("", ArticlePostApiResponse::class.java)

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        em.clear()
        val foundOne = articleRepository.findById(ArticleId(deSerializedResponse.articleId))
        assertThat(foundOne).isNotNull
        assertThat(foundOne!!.id).isEqualTo(ArticleId(deSerializedResponse.articleId))
        assertThat(foundOne.title).isEqualTo("test_d0ce030f400d")
    }

    @Test
    fun `post Article 수정 테스트`() {
// given:
        val articleId = ArticleId(1)
        articleRepository.save(
            Article.Factory.newPublishedArticle(
                id = articleId,
                writerId = UserId(1),
                title = "test_d0ce030f400d",
                content = "test_d0ce030f400d",
                thumbnailUrl = "https://example.com/thumbnail.png",
                categoryId = CategoryId(1),
                tags = setOf(TagId(3)),
                draftArticleId = null
            )
        )
        em.clear()

        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")
            .body(
                """{
    "writerId": 1,
    "articleId": ${articleId.value},
    "title": "edit",
    "content": "edit",
    "thumbnailUrl": "https://example.com/thumbnail.png",
    "categoryId": 1,
    "tags": [
      1,
      2,
      3
    ]   
    }""".trimIndent()
            )

        // when:
        val response = given().spec(request)
            .`when`().put("api/articles/${articleId.value}")
            .then().extract()

        val deSerializedResponse = response.body().jsonPath().getObject("", ArticleEditApiResponse::class.java)

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        em.clear()
        val foundOne = articleRepository.findById(ArticleId(deSerializedResponse.articleId))
        assertThat(foundOne).isNotNull
        assertThat(foundOne!!.id).isEqualTo(ArticleId(deSerializedResponse.articleId))
        assertThat(foundOne.title).isEqualTo("edit")
    }

    @Test
    fun `article delete 테스트`() {
        // given:
        val articleId = ArticleId(1)
        articleRepository.save(
            Article.Factory.newPublishedArticle(
                id = articleId,
                writerId = UserId(1),
                title = "test_d0ce030f400d",
                content = "test_d0ce030f400d",
                thumbnailUrl = "https://example.com/thumbnail.png",
                categoryId = CategoryId(1),
                tags = setOf(TagId(3)),
                draftArticleId = null
            )
        )
        em.clear()

        // when:
        val response = given()
            .`when`().delete("api/articles/${articleId.value}")
            .then().extract()

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        em.clear()
        val foundOne = articleRepository.findById(articleId)
        assertThat(foundOne).isNull()
    }
}