package kr.co.jiniaslog.blog.usecase

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryTitle
import kr.co.jiniaslog.blog.domain.category.dto.CategoryDataHolder
import kr.co.jiniaslog.blog.domain.toDto
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import kr.co.jiniaslog.blog.outbound.CategoryRepository
import kr.co.jiniaslog.blog.usecase.category.ISyncCategories
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ISyncCategoriesUseCaseTests : TestContainerAbstractSkeleton() {
    @Autowired
    private lateinit var sut: ISyncCategories

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var transactionHandler: BlogTransactionHandler

    @Autowired
    private lateinit var em: EntityManager

    @Test
    fun `기존 카테고리가 존재하고 새로운 카테고리가 추가되면 동기화가 이루어진다`() {
        // given
        val (parent1, parent2) = transactionHandler.run {
            asIsPersistedCategories()
        }

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
        sut.handle(ISyncCategories.Command(toBeVos))

        // then
        val result = categoryRepository.findAll()
        result.size shouldBe 5
        val addedTarget = result.filter { it.categoryTitle == CategoryTitle("child3") }.first()
        addedTarget.sortingPoint shouldBe 0
        val parent = addedTarget.parent
        parent.shouldNotBeNull()
        parent.entityId shouldBe parent2.entityId
    }

    @Test
    fun `기존 카테고리가 존재하고 새로운 카테고리가 다른 정렬순서로 주어지면 동기화된다`() {
        // given
        val (parent1, parent2) = asIsPersistedCategories()

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
                categoryName = CategoryTitle("P3"),
                sortingPoint = 2,
            )

        val toBeVos =
            listOf(
                parent2Vo,
                parent1Vo,
                parent3Vo,
            )

        // when
        sut.handle(ISyncCategories.Command(toBeVos))

        // then
        val result = categoryRepository.findAll()
        result.size shouldBe 6
        val addedTarget = result.first { it.categoryTitle == CategoryTitle("child3") }
        addedTarget.sortingPoint shouldBe 0

        val parent = addedTarget.parent
        parent.shouldNotBeNull()
        parent.entityId shouldBe parent2.entityId

        val parent3 = result.first { it.categoryTitle == CategoryTitle("P3") }

        parent3.sortingPoint shouldBe 2
    }

    @Test
    fun `기존 카테고리에서 추가 및 변경시에도 동기화가 이루어진다`() {
        val (parent1, parent2) = asIsPersistedCategories()

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
        sut.handle(ISyncCategories.Command(toBeVos))

        // then
        transactionHandler.runInRepeatableReadTransaction {
            val result = categoryRepository.findAll()
            result.size shouldBe 6
            val addedTarget = result.first { it.categoryTitle == CategoryTitle("child3") }
            addedTarget.sortingPoint shouldBe 0
            val parent = addedTarget.parent
            parent.shouldNotBeNull()
            parent.entityId shouldBe parent2.entityId

            val edited = result.first { it.categoryTitle == CategoryTitle("수정") }
            edited.children.size shouldBe 1
        }
    }

    @Test
    fun `기존 카테고리에서 일부가 삭제되는 요청시 동기화가 이루어진다`() {
        // given
        val (parent1, parent2) = asIsPersistedCategories()

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
        sut.handle(ISyncCategories.Command(toBeVos))

        // then
        val result = categoryRepository.findAll()
        result.size shouldBe 3
    }

    private fun asIsPersistedCategories(): Pair<Category, Category> {
        val parent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("parent1"),
                0,
            )
        categoryRepository.save(parent1)

        val child1OfParent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("child1"),
                1,
            ).apply {
                changeParent(parent1)
            }
        categoryRepository.save(child1OfParent1)

        val child2OfParent1 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("child2"),
                1,
            ).apply {
                changeParent(parent1)
            }
        categoryRepository.save(child2OfParent1)

        val parent2 =
            Category(
                CategoryId(IdUtils.generate()),
                CategoryTitle("parent2"),
                0,
            )
        categoryRepository.save(parent2)
        return Pair(parent1, parent2)
    }

    @Test
    fun `새로운 카테고리들이 연관관계를 가지고 있어도 정상 저장된다`() {
        // given
        val parent1 = CategoryDataHolder(
            categoryId = null,
            categoryName = CategoryTitle("parent1"),
            sortingPoint = 0,
            children = listOf(
                CategoryDataHolder(
                    categoryId = null,
                    categoryName = CategoryTitle("child1"),
                    sortingPoint = 1,
                    children = listOf(
                        CategoryDataHolder(
                            categoryId = null,
                            categoryName = CategoryTitle("child2"),
                            sortingPoint = 2,
                        )
                    )
                )
            )
        )

        val parent2 = CategoryDataHolder(
            categoryId = null,
            categoryName = CategoryTitle("parent2"),
            sortingPoint = 0,
            children = listOf(
                CategoryDataHolder(
                    categoryId = null,
                    categoryName = CategoryTitle("child1"),
                    sortingPoint = 1,
                    children = listOf(
                        CategoryDataHolder(
                            categoryId = null,
                            categoryName = CategoryTitle("child2"),
                            sortingPoint = 2,
                        )
                    )
                )
            )
        )

        // when
        sut.handle(
            ISyncCategories.Command(
                listOf(
                    parent1,
                    parent2,
                )
            )
        )

        // then
        em.clear()
        val all = categoryRepository.findAll()
        all.size shouldBe 6
    }
}
