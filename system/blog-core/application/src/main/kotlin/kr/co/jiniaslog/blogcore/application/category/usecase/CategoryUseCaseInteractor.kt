package kr.co.jiniaslog.blogcore.application.category.usecase

import kr.co.jiniaslog.blogcore.application.category.usecase.CategoryCommands.CategoryVo
import kr.co.jiniaslog.blogcore.application.category.usecase.CategoryCommands.SyncCategoryCommand
import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.category.Category
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.category.CategoryIdGenerator
import kr.co.jiniaslog.blogcore.domain.category.CategoryRepository

class CategoryUseCaseInteractor(
    private val categoryIdGenerator: CategoryIdGenerator,
    private val categoryRepository: CategoryRepository,
    private val transactionHandler: TransactionHandler,
) : CategoryCommands {
    override fun syncCategories(command: SyncCategoryCommand) = with(command) {
        val existingCategories = categoryRepository.findAll()

        transactionHandler.runInReadCommittedTransaction {
            command.upsert()
            command.delete(existingCategories)
        }
    }

    private fun SyncCategoryCommand.upsert() =
        categoryVos.forEach { categoryVo ->
            val id = categoryVo.id ?: categoryIdGenerator.generate()
            categoryRepository.save(categoryVo.toDomain(id))
        }

    private fun CategoryVo.toDomain(id: CategoryId): Category = Category.from(
        id = id,
        label = label,
        parentId = parentId,
        order = order,
        createdAt = createAt,
        updatedAt = updatedAt,
    )

    private fun SyncCategoryCommand.delete(existingCategories: List<Category>) =
        existingCategories.filter { existingCategory ->
            categoryVos.notContains(existingCategory.id)
        }.forEach { categoryToDelete ->
            categoryRepository.delete(categoryToDelete.id)
        }

    private fun List<CategoryVo>.notContains(id: CategoryId): Boolean =
        this.none { categoryVo -> categoryVo.id == id }
}
