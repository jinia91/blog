package kr.co.jiniaslog.blog.domain.category

import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.blog.domain.CategoryTestFixtures
import kr.co.jiniaslog.blog.domain.category.dto.CategoryDataHolder
import kr.co.jiniaslog.blog.domain.toDto
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.junit.jupiter.api.Test

class CategorySyncerTest : SimpleUnitTestContext() {
    private val sut = CategorySyncer()

    @Test
    fun `기존 카테고리가 존재하고 새로운 카테고리가 추가되면 동기화가 이루어진다`() {
        // given
        val parent1 = CategoryTestFixtures.createCategory()

        val child1OfParent1 = CategoryTestFixtures.createCategory(
            parent = parent1
        )
        val child2OfParent1 = CategoryTestFixtures.createCategory(
            sortingPoint = 1,
            parent = parent1
        )

        val parent2 = CategoryTestFixtures.createCategory(
            sortingPoint = 1,
        )

        val asIsList =
            listOf(
                parent1,
                child1OfParent1,
                child2OfParent1,
                parent2,
            )

        val parent1Vo = parent1.toDto()
        var parent2Vo = parent2.toDto()

        val newChild3Parent2 = CategoryDataHolder(
            categoryId = null,
            categoryName = CategoryTitle("child3"),
            sortingPoint = 0,
        )
        parent2Vo =
            parent2Vo.copy(
                children = parent2Vo.children + listOf(newChild3Parent2),
            )

        val toBeVos =
            listOf(
                parent1Vo,
                parent2Vo,
            )

        // when
        val result =
            sut.syncCategories(
                asIsList,
                toBeVos,
            )

        // then
        result.toBeDelete.isEmpty() shouldBe true
        result.toBeInsert.isNotEmpty() shouldBe true
        result.toBeInsert.size shouldBe 1
        result.toBeUpdate.isNotEmpty() shouldBe true
        result.toBeUpdate.size shouldBe 4
    }

    @Test
    fun `기존 카테고리가 존재하고 새로운 카테고리가 추가되며 무작위로 정렬되어 있어도 동기화는 이루어진다`() {
        // given
        val parent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("parent1"),
                0,
            )

        val child1OfParent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("child1"),
                1,
            ).apply {
                changeParent(parent1)
            }

        val child2OfParent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("child2"),
                1,
            ).apply {
                changeParent(parent1)
            }

        val parent2 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("parent2"),
                0,
            )

        val asIsList =
            listOf(
                parent1,
                child1OfParent1,
                child2OfParent1,
                parent2,
            )

        val parent1Vo = parent1.toDto()
        var parent2Vo = parent2.toDto()
        val newChild3Parent2 =
            CategoryDataHolder(
                categoryId = null,
                categoryName = CategoryTitle("child3"),
                sortingPoint = 0,
            )
        parent2Vo =
            parent2Vo.copy(
                children = parent2Vo.children + listOf(newChild3Parent2),
            )
        val parent3Vo =
            CategoryDataHolder(
                categoryId = null,
                categoryName = CategoryTitle("child3"),
                sortingPoint = 2,
            )

        val toBeVos =
            listOf(
                parent2Vo,
                parent1Vo,
                parent3Vo,
            )

        // when
        val result =
            sut.syncCategories(
                asIsList,
                toBeVos,
            )

        // then
        result.toBeDelete.isEmpty() shouldBe true
        result.toBeUpdate.isNotEmpty() shouldBe true
        result.toBeInsert.isNotEmpty() shouldBe true

        result.toBeInsert.size shouldBe 2
        result.toBeUpdate.size shouldBe 4
    }

    @Test
    fun `기존 카테고리가 존재하고 새로운 카테고리가 추가되며, 기존 카테고리가 변경되어도 동기화가 이루어진다`() {
        // given
        val parent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("parent1"),
                0,
            )

        val child1OfParent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("child1"),
                1,
            ).apply {
                changeParent(parent1)
            }

        val child2OfParent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("child2"),
                1,
            ).apply {
                changeParent(parent1)
            }

        val parent2 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("parent2"),
                0,
            )

        val asIsList =
            listOf(
                parent1,
                child1OfParent1,
                child2OfParent1,
                parent2,
            )

        val parent1Vo = parent1.toDto()
        var parent2Vo = parent2.toDto()
        val newChild3Parent2 =
            CategoryDataHolder(
                categoryId = null,
                categoryName = CategoryTitle("child3"),
                sortingPoint = 0,
            )
        parent2Vo =
            parent2Vo.copy(
                children = parent2Vo.children + listOf(newChild3Parent2),
                categoryName = CategoryTitle("수정"),
            )
        val parent3Vo =
            CategoryDataHolder(
                categoryId = null,
                categoryName = CategoryTitle("child3-ㄷ"),
                sortingPoint = 2,
            )

        val toBeVos =
            listOf(
                parent2Vo,
                parent1Vo,
                parent3Vo,
            )

        // when
        val result =
            sut.syncCategories(
                asIsList,
                toBeVos,
            )

        // then
        result.toBeDelete.isEmpty() shouldBe true
        result.toBeUpdate.isNotEmpty() shouldBe true
        result.toBeUpdate.size shouldBe 4
        result.toBeUpdate.first { it.entityId == parent2.entityId }.categoryTitle.value shouldBe "수정"

        result.toBeInsert.isNotEmpty() shouldBe true
        result.toBeInsert.size shouldBe 2
    }

    @Test
    fun `기존 카테고리가 존재하고 새로운 카테고리가 추가되며, 기존카테고리가 삭제되고 기존 카테고리가 변경되어도 동기화가 이루어진다`() {
        // given
        val parent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("parent1"),
                0,
            )

        val child1OfParent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("child1"),
                1,
            ).apply {
                changeParent(parent1)
            }

        val child2OfParent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("child2"),
                1,
            ).apply {
                changeParent(parent1)
            }

        val parent2 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("parent2"),
                0,
            )

        val asIsList =
            listOf(
                parent1,
                child1OfParent1,
                child2OfParent1,
                parent2,
            )

        val parent1Vo = parent1.toDto()
        var parent2Vo = parent2.toDto()
        val newChild3Parent2 =
            CategoryDataHolder(
                categoryId = null,
                categoryName = CategoryTitle("child3"),
                sortingPoint = 0,
            )
        parent2Vo =
            parent2Vo.copy(
                children = parent2Vo.children + listOf(newChild3Parent2),
                categoryName = CategoryTitle("수정"),
            )
        val parent3Vo =
            CategoryDataHolder(
                categoryId = null,
                categoryName = CategoryTitle("child3-ㄷ"),
                sortingPoint = 2,
            )

        val toBeVos =
            listOf(
                parent2Vo,
                parent3Vo,
            )

        // when
        val result =
            sut.syncCategories(
                asIsList,
                toBeVos,
            )

        // then
        result.toBeDelete.isNotEmpty() shouldBe true
        result.toBeDelete.size shouldBe 3
        result.toBeUpdate.isNotEmpty() shouldBe true
        result.toBeUpdate.size shouldBe 1
        result.toBeInsert.isNotEmpty() shouldBe true
        result.toBeInsert.size shouldBe 2
        result.toBeUpdate.first { it.entityId == parent2.entityId }.categoryTitle.value shouldBe "수정"
    }
}
