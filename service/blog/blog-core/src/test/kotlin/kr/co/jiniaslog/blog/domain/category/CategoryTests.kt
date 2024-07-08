package kr.co.jiniaslog.blog.domain.category

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CategoryTests {
    @Test
    fun `유효한 카테고리 요청이 있으면 카테고리가 생성된다`() {
        // given
        val categoryTitle = CategoryTitle("카테고리")
        val sortingPoint = 0
        val parent = null

        // when
        val category = Category(
            categoryTitle = categoryTitle,
            sortingPoint = sortingPoint,
            parent = parent,
            categoryId = CategoryId(1)
        )

        // then
        category.categoryTitle shouldBe categoryTitle
        category.sortingPoint shouldBe sortingPoint
        category.parent shouldBe null
        category.id shouldBe CategoryId(1)
    }
}
