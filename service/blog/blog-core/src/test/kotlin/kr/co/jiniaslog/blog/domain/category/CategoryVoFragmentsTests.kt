package kr.co.jiniaslog.blog.domain.category

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class CategoryVoFragmentsTests {
    @Test
    fun `카테고리 이름이 20자를 넘으면 생성할 수 없다`() {
        val longCategoryName = "a".repeat(21)
        shouldThrow<IllegalArgumentException> {
            CategoryTitle(longCategoryName)
        }
    }

    @Test
    fun `카테고리 이름은 비어있을 수 없다`() {
        shouldThrow<IllegalArgumentException> {
            CategoryTitle("")
        }
    }

    @Test
    fun `카테고리 id는 0이 될 수 없다`() {
        shouldThrow<IllegalArgumentException> {
            CategoryId(0)
        }
    }
}
