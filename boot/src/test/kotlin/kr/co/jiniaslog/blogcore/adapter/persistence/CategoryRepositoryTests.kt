package kr.co.jiniaslog.blogcore.adapter.persistence

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.jiniaslog.blogcore.domain.category.Category
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.category.CategoryRepository
import kr.co.jiniaslog.config.TestContainerConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CategoryRepositoryTests : TestContainerConfig() {
    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @PersistenceContext(unitName = CoreDB.PERSISTENT_UNIT)
    private lateinit var em: EntityManager

    @Test
    fun `category save test`() {
        // given: when
        categoryRepository.save(
            Category.newOne(
                id = CategoryId(1),
                label = "상위 카테고리 1",
                order = 1,
                parentId = null,
            )
        )

        categoryRepository.save(
            Category.newOne(
                id = CategoryId(2),
                label = "상위 카테고리 2",
                order = 2,
                parentId = null,
            )
        )

        categoryRepository.save(
            Category.newOne(
                id = CategoryId(3),
                label = "하위 기존 카테고리 1",
                order = 5,
                parentId = CategoryId(1),
            )
        )

        em.clear()

        // then
        val categories = categoryRepository.findAll()
        Assertions.assertThat(categories.size).isEqualTo(3)
    }

    @Test
    fun `find cateogry by label test`(){
        // given: when
        categoryRepository.save(
            Category.newOne(
                id = CategoryId(1),
                label = "상위 카테고리 1",
                order = 1,
                parentId = null,
            )
        )

        categoryRepository.save(
            Category.newOne(
                id = CategoryId(2),
                label = "상위 카테고리 2",
                order = 2,
                parentId = null,
            )
        )

        categoryRepository.save(
            Category.newOne(
                id = CategoryId(3),
                label = "하위 기존 카테고리 1",
                order = 5,
                parentId = CategoryId(1),
            )
        )

        em.clear()

        // then
        val category = categoryRepository.findByLabel("상위 카테고리 1")
        Assertions.assertThat(category).isNotNull
        Assertions.assertThat(category?.label).isEqualTo("상위 카테고리 1")
    }
}