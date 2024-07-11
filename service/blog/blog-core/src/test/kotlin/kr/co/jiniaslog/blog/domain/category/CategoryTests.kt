package kr.co.jiniaslog.blog.domain.category

import io.kotest.assertions.throwables.shouldThrow
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
            id = CategoryId(1)
        )

        // then
        category.categoryTitle shouldBe categoryTitle
        category.sortingPoint shouldBe sortingPoint
        category.parent shouldBe null
        category.entityId shouldBe CategoryId(1)
    }

    @Test
    fun `카테고리의 정렬 포인트는 음수일 수 없다`() {
        // given
        val categoryTitle = CategoryTitle("카테고리")
        val sortingPoint = -1
        val parent = null

        // when, then
        shouldThrow<IllegalArgumentException> {
            Category(
                categoryTitle = categoryTitle,
                sortingPoint = sortingPoint,
                parent = parent,
                id = CategoryId(1)
            )
        }
    }
}
