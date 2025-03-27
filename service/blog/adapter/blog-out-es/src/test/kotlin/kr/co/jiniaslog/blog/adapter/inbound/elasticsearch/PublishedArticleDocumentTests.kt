package kr.co.jiniaslog.blog.adapter.inbound.elasticsearch

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Month

class PublishedArticleDocumentTests {
    @Test
    fun `타임스탬프를 kst 시간으로 제대로 변환할 수 있다`() {
        // given
        val publishedArticleDocument = PublishedArticleDocument(
            id = "1",
            title = "제목",
            content = "내용",
            status = "PUBLISHED",
            thumbnailUrl = "https://thumbnail.url",
            createdAtTimeStamp = 1743048526037,
            updatedAtTimeStamp = 1743048526037
        )

        // when
        val createdAt = publishedArticleDocument.createdAt
        println(createdAt)

        // then
        createdAt.year shouldBe 2025
        createdAt.month shouldBe Month.MARCH
        createdAt.dayOfMonth shouldBe 27
        createdAt.hour shouldBe 13
        createdAt.minute shouldBe 8
    }
}
