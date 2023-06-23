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
        command.validate()
        val (newCategoryVos, toBeUpdatedCategoryVos) = categoryVos.partition { it.id == null }
        val toBeDeletedCategories = categoryRepository.findAll()
            .filter { existingCategory ->
                categoryVos.notContains(existingCategory.id)
            }

        transactionHandler.runInReadCommittedTransaction {
            save(newCategoryVos)
            update(toBeUpdatedCategoryVos)
            delete(toBeDeletedCategories)
        }
    }

    private fun SyncCategoryCommand.validate() {
        TODO("Not yet implemented")
    }

    private fun save(newCategoryVos: List<CategoryVo>) {
        newCategoryVos.forEach { categoryVo ->
            val id = categoryIdGenerator.generate()
            categoryRepository.save(categoryVo.toDomain(id))
        }
    }

    private fun update(toBeUpdateCategoryVos: List<CategoryVo>) {
        toBeUpdateCategoryVos.forEach { categoryVo ->
            categoryRepository.save(categoryVo.toDomain(categoryVo.id!!))
        }
    }

    private fun delete(toBeDeleteCategories: List<Category>) {
        toBeDeleteCategories.forEach { categoryToDelete ->
            categoryRepository.delete(categoryToDelete.id)
        }
    }

    private fun CategoryVo.toDomain(id: CategoryId): Category = Category.from(
        id = id,
        label = label,
        parentId = parentId,
        order = order,
        createdAt = createAt,
        updatedAt = updatedAt,
    )
    private fun List<CategoryVo>.notContains(id: CategoryId): Boolean =
        this.none { categoryVo -> categoryVo.id == id }
}
