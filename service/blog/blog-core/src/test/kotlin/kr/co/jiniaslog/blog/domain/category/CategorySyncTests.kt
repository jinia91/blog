package kr.co.jiniaslog.blog.domain.category

import io.kotest.assertions.withClue
import kr.co.jiniaslog.blog.domain.CategoryTestFixtures
import kr.co.jiniaslog.blog.domain.category.dto.CategoryDto
import kr.co.jiniaslog.blog.domain.toDto
import org.junit.jupiter.api.Test

class CategorySyncTests {
    private val sut: CategorySyncer = CategorySyncer()

    @Test
    fun `기존에 없는 카테고리는 새롭게 생성된다`() {
        // given
        val asIsCategories = withClue("기존에 존재하는 카테고리들") {
            val parent1 = CategoryTestFixtures.createCategory()
            val parent2 = CategoryTestFixtures.createCategory()

            mutableListOf(
                parent1,
                CategoryTestFixtures.createCategory(parent = parent1),
                CategoryTestFixtures.createCategory(parent = parent1),
                CategoryTestFixtures.createCategory(parent = parent1),
                parent2,
                CategoryTestFixtures.createCategory(parent = parent2),
                CategoryTestFixtures.createCategory(parent = parent2),
            )
        }

        val toBeCategories = withClue("기존 카테고리에서 새로운카테고리만 추가") {
            val parent1 = CategoryTestFixtures.createCategory().toDto()
            val parent2 = CategoryTestFixtures.createCategory().toDto()
            val newOneParent = CategoryDto(
                title = CategoryTitle("newOne"),
                sortingPoint = 3
            )

            mutableListOf(
                parent1,
                CategoryTestFixtures.createCategory(parent = parent1),
                CategoryTestFixtures.createCategory(parent = parent1),
                CategoryTestFixtures.createCategory(parent = parent1),
                CategoryTestFixtures.createCategory(parent = parent1), // newOne
                parent2,
                CategoryTestFixtures.createCategory(parent = parent2),
                CategoryTestFixtures.createCategory(parent = parent2),
                newOneParent
            )
        }

        // when
        sut.sync(asIsCategories,)
    }
}
