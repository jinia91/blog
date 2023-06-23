package kr.co.jiniaslog.blogcore.application.category.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.category.Category
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.category.CategoryIdGenerator
import kr.co.jiniaslog.blogcore.domain.category.CategoryRepository
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import utils.TestUtils
import java.time.LocalDateTime

class CategoryUseCaseInteractorTest : BehaviorSpec() {
    private val categoryIdGenerator: CategoryIdGenerator = mockk()
    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val transactionHandler: TransactionHandler = mockk()
    private val sut: CategoryUseCaseInteractor = CategoryUseCaseInteractor(
        categoryIdGenerator = categoryIdGenerator,
        categoryRepository = categoryRepository,
        transactionHandler = transactionHandler,
    )

    init {
        TestUtils.doTransactionDefaultStubbing(transactionHandler)

        Given("기존 카테고리가 존재하고") {
            val dummy1 = Category.from(
                id = CategoryId(1),
                label = "dummy1",
                parentId = null,
                order = 1,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

            val dummy2 = Category.from(
                id = CategoryId(2),
                label = "dummy2",
                parentId = CategoryId(1),
                order = 2,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

            val dummy3 = Category.from(
                id = CategoryId(3),
                label = "dummy3",
                parentId = CategoryId(1),
                order = 3,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

            And("기존카테고리 데이터와 새로운 카테고리 데이터가 주어지고") {
                val dummy1Data = CategoryCommands.CategoryData(
                    id = dummy1.id,
                    label = dummy1.label,
                    parentId = dummy1.parentId,
                    order = dummy1.order,
                    createAt = dummy1.createdAt,
                    updatedAt = dummy1.updatedAt,
                )

                val dummy2Data = CategoryCommands.CategoryData(
                    id = dummy2.id,
                    label = dummy2.label,
                    parentId = dummy2.parentId,
                    order = dummy2.order,
                    createAt = dummy2.createdAt,
                    updatedAt = dummy2.updatedAt,
                )

                val dummy3Data = CategoryCommands.CategoryData(
                    id = dummy3.id,
                    label = dummy3.label,
                    parentId = dummy3.parentId,
                    order = dummy3.order,
                    createAt = dummy3.createdAt,
                    updatedAt = dummy3.updatedAt,
                )

                val newData = CategoryCommands.CategoryData(
                    id = null,
                    label = "newOne",
                    parentId = null,
                    order = 1,
                    createAt = null,
                    updatedAt = null,
                )

                And("기존카테고리는 없는 command가 존재하면") {
                    val command = CategoryCommands.SyncCategoryCommand(
                        categoriesData = listOf(newData),
                    )
                    every { categoryIdGenerator.generate() } returns CategoryId(value = 4)
                    every { categoryRepository.findAll() } returns listOf(dummy1, dummy2, dummy3)

                    When("카테고리를 동기화하면") {
                        sut.syncCategories(command)
                        Then("신규 카테고리가 추가되며, 기존 카테고리는 삭제된다") {
                            verify(exactly = 1) { categoryRepository.save(any()) }
                            verify(exactly = 0) { categoryRepository.update(any()) }
                            verify(exactly = 3) { categoryRepository.delete(any()) }
                        }
                    }
                }

                clearMocks(categoryRepository)
                And("기존 카테고리가 존재하는 command가 존재하면") {
                    val command = CategoryCommands.SyncCategoryCommand(
                        categoriesData = listOf(newData, dummy1Data, dummy2Data, dummy3Data),
                    )
                    every { categoryIdGenerator.generate() } returns CategoryId(value = 4)
                    every { categoryRepository.findAll() } returns listOf(dummy1, dummy2, dummy3)

                    When("카테고리를 동기화하면") {
                        sut.syncCategories(command)
                        Then("신규 카테고리가 추가되며, 기존 카테고리는 삭제되지 않고 업데이트된다") {
                            verify(exactly = 1) { categoryRepository.save(any()) }
                            verify(exactly = 3) { categoryRepository.update(any()) }
                            verify(exactly = 0) { categoryRepository.delete(any()) }
                        }
                    }
                }

                clearMocks(categoryRepository)
                And("기존 카테고리가 일부 존재하는 command가 존재하면") {
                    val command = CategoryCommands.SyncCategoryCommand(
                        categoriesData = listOf(newData, dummy2Data, dummy3Data),
                    )
                    every { categoryIdGenerator.generate() } returns CategoryId(value = 4)
                    every { categoryRepository.findAll() } returns listOf(dummy1, dummy2, dummy3)

                    When("카테고리를 동기화하면") {
                        sut.syncCategories(command)
                        Then("신규 카테고리가 추가되며, 기존 카테고리중 없는 카테고리는 삭제되고, 나머진 업데이트된다") {
                            verify(exactly = 1) { categoryRepository.save(any()) }
                            verify(exactly = 2) { categoryRepository.update(any()) }
                            verify(exactly = 1) { categoryRepository.delete(any()) }
                        }
                    }
                }

                clearMocks(categoryRepository)
                And("기존 카테고리가 존재하는 command가 존재하고") {
                    val command = CategoryCommands.SyncCategoryCommand(
                        categoriesData = listOf(newData, dummy1Data, dummy2Data, dummy3Data),
                    )
                    every { categoryIdGenerator.generate() } returns CategoryId(value = 4)

                    And("실제론 기존 카테고리가 존재하지 않으면") {
                        every { categoryRepository.findAll() } returns emptyList()
                        When("카테고리를 동기화하면") {
                            Then("예외가 발생한다.") {
                                shouldThrow<ResourceNotFoundException> {
                                    sut.syncCategories(command)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
