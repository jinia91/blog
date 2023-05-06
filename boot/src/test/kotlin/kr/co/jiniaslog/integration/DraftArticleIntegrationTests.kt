package kr.co.jiniaslog.integration

import kr.co.jiniaslog.config.TestContainerConfig
import io.restassured.RestAssured.given
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.adapter.http.draft.DraftArticleCreateApiResponse
import kr.co.jiniaslog.blogcore.adapter.http.draft.DraftArticleGetApiResponse
import kr.co.jiniaslog.blogcore.adapter.http.draft.DraftArticleUpdateApiResponse
import kr.co.jiniaslog.blogcore.adapter.persistence.CoreDB
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.PublishedArticleCreatedEvent
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import org.apache.http.protocol.HTTP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.testcontainers.shaded.org.apache.commons.lang3.event.EventUtils
import org.testcontainers.shaded.org.bouncycastle.asn1.x500.style.RFC4519Style.title
import java.time.LocalDateTime

class DraftArticleIntegrationTests : TestContainerConfig() {

    @Autowired
    private lateinit var draftArticleRepository: DraftArticleRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var domainEventPublisher: DomainEventPublisher

    @Test
    fun `draft Article 저장 테스트`() {
        // given:
        val title = "제목입니다~"
        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")
            .body("""
{
  "writerId": 1,
  "title": "$title",
  "content": "내용",
  "thumbnailUrl": "https://example.com/thumbnail.png"
}
""".trimIndent())

        // when:
        val response = given().spec(request)
            .`when`().post("/api/articles/draft")
            .then().extract()

        val responseDto = response.response()
        .`as`(DraftArticleCreateApiResponse::class.java)

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        em.clear()
        val temp = draftArticleRepository.getById(DraftArticleId(responseDto.draftArticleId))
        assertThat(temp).isNotNull
        assertThat(temp!!.id).isEqualTo(DraftArticleId(responseDto.draftArticleId))
        assertThat(temp.title).isEqualTo(title)
    }

    @Test
    fun `draft article이 조회 테스트`() {
        // given:
        val draftArticleId = DraftArticleId(1541L)

        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")
            .queryParam("draftArticleId", draftArticleId.value)

        draftArticleRepository.save(
            DraftArticle.Factory.newOne(
                writerId = UserId(value = 5626),
                title = "test",
                content = "test",
                thumbnailUrl = null,
                id = draftArticleId
            )
        )

        em.clear()

        // when:
        val response = given().spec(request)
            .`when`().get("/api/articles/draft/${draftArticleId.value}")
            .then().extract()

        val responseDto = response.response()
            .`as`(DraftArticleGetApiResponse::class.java)

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        assertThat(responseDto.title).isEqualTo("test")
        assertThat(responseDto.content).isEqualTo("test")
    }

    @Test
    fun `draft Article 수정 테스트`(){
        // given:
        val draftArticleId = DraftArticleId(1541L)
        val title = "제목입니다~"
        draftArticleRepository.save(
            DraftArticle.Factory.newOne(
                writerId = UserId(value = 5626),
                title = "test",
                content = "test",
                thumbnailUrl = null,
                id = draftArticleId
            )
        )
        em.clear()

        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")
            .body("""
{
    "writerId": 1,
    "title": "$title",
    "content": "내용",
    "thumbnailUrl": "https://example.com/thumbnail.png"
    }
    """)

        // when:
        val response = given().spec(request)
            .`when`().put("/api/articles/draft/${draftArticleId.value}")
            .then().extract()

        val responseDto = response.response()
            .`as`(DraftArticleUpdateApiResponse::class.java)

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        assertThat(responseDto.draftArticleId).isEqualTo(draftArticleId.value)
    }

    @Test
    fun `draft Article 삭제 테스트`(){
        // given:
        val draftArticleId = DraftArticleId(1541L)
        draftArticleRepository.save(
            DraftArticle.Factory.newOne(
                writerId = UserId(value = 5626),
                title = "test",
                content = "test",
                thumbnailUrl = null,
                id = draftArticleId
            )
        )
        em.clear()

        val request = given()
            .header(HTTP.CONTENT_TYPE, "application/json")

        // when:
        val response = given().spec(request)
            .`when`().delete("/api/articles/draft/${draftArticleId.value}")
            .then().extract()

        // then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        assertThat(draftArticleRepository.getById(draftArticleId)).isNull()
    }

    @Test
    fun `publishedArticle 생성시 이벤트 리슨으로 draftArticle 삭제 테스트`() {
        val draftArticleId = DraftArticleId(7777L)

        draftArticleRepository.save(
            DraftArticle.Factory.newOne(
                writerId = UserId(value = 5626),
                title = "test",
                content = "test",
                thumbnailUrl = null,
                id = draftArticleId
            )
        )
        em.clear()

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        domainEventPublisher.publish(
             PublishedArticleCreatedEvent(
                ArticleId(1L),
                UserId(1L),
                DraftArticleId(7777L),
                LocalDateTime.now()
            )
        )

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        assertThat(draftArticleRepository.getById(draftArticleId)).isNull()
    }
}