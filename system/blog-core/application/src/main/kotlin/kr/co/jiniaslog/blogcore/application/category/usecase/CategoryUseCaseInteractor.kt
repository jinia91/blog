package kr.co.jiniaslog.blogcore.application.category.usecase

import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.category.Category
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.category.CategoryIdGenerator
import kr.co.jiniaslog.blogcore.domain.category.CategoryRepository
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor

@UseCaseInteractor
class CategoryUseCaseInteractor(
    private val categoryIdGenerator: CategoryIdGenerator,
    private val categoryRepository: CategoryRepository,
    private val transactionHandler: TransactionHandler,
) : CategoryCommands {
    override fun syncCategories(command: CategoryCommands.SyncCategoryCommand) = with(command) {
        val existingCategories = categoryRepository.findAll()

        transactionHandler.runInReadCommittedTransaction {
            upsert()
            delete(existingCategories)
        }
    }

    private fun CategoryCommands.SyncCategoryCommand.upsert() =
        categoryVos.forEach { categoryVo ->
            val id = categoryVo.id ?: categoryIdGenerator.generate()
            categoryRepository.save(categoryVo.toDomain(id))
        }

    private fun CategoryCommands.SyncCategoryCommand.delete(existingCategories: List<Category>) =
        existingCategories.filter { existingCategory ->
            categoryVos.notContains(existingCategory.id)
        }.forEach { categoryToDelete ->
            categoryRepository.delete(categoryToDelete.id)
        }

    private fun List<CategoryCommands.CategoryVo>.notContains(id: CategoryId): Boolean =
        this.none { categoryVo -> categoryVo.id == id }
}
