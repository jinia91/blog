package kr.co.jiniaslog.blog.domain.article

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class ArticleVoFragmentsTests {
    @Test
    fun `게시글 제목이 100자를 넘으면 생성할 수 없다`() {
        val title = "a".repeat(101)
        shouldThrow<IllegalArgumentException> {
            ArticleContents(
                title = title,
                contents = "contents",
                thumbnailUrl = "thumbnailUrl"
            )
        }
    }

    @Test
    fun `게시글 아이디는 0이 될 수 없다`() {
        shouldThrow<IllegalArgumentException> {
            ArticleId(0)
        }
    }
}
