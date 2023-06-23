package kr.co.jiniaslog.blogcore.application.category.usecase

import kr.co.jiniaslog.blogcore.application.category.usecase.CategoryCommands.CategoryData
import kr.co.jiniaslog.blogcore.application.category.usecase.CategoryCommands.SyncCategoryCommand
import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.category.Category
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.category.CategoryIdGenerator
import kr.co.jiniaslog.blogcore.domain.category.CategoryRepository
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kr.co.jiniaslog.shared.core.domain.ValidationException

@UseCaseInteractor
class CategoryUseCaseInteractor(
    private val categoryIdGenerator: CategoryIdGenerator,
    private val categoryRepository: CategoryRepository,
    private val transactionHandler: TransactionHandler,
) : CategoryCommands, CategoryQueries {
    override fun syncCategories(command: SyncCategoryCommand) = with(command) {
        command.validate()
        val (newCategoriesData, toBeUpdatedCategoriesData) = categoriesData.partition { it.isNew }
        val existingCategories = categoryRepository.findAll()
        val toBeUpdatedCategoriesAndData = toBeUpdatedCategoriesData.map { data ->
            val category = existingCategories.find { it.id == data.id }
                ?: throw ResourceNotFoundException("category ${data.id} not found")
            data to category
        }
        val toBeDeletedCategories = existingCategories.filter { existingCategory ->
            categoriesData.notContains(existingCategory.id)
        }

        transactionHandler.runInReadCommittedTransaction {
            save(newCategoriesData)
            update(toBeUpdatedCategoriesAndData)
            delete(toBeDeletedCategories)
        }
    }

    private fun SyncCategoryCommand.validate() {
        val groupedByParent = categoriesData.groupBy { it.parentId }
        for (categories in groupedByParent.values) {
            if (categories.hasDuplicateOrder()) throw ValidationException("Order conflict")
        }
    }

    private fun List<CategoryData>.hasDuplicateOrder(): Boolean {
        val orders = this.map { it.order }
        return orders.size != orders.toSet().size
    }

    private fun save(newCategoryVos: List<CategoryData>) {
        newCategoryVos.forEach { categoryVo ->
            val id = categoryIdGenerator.generate()
            categoryRepository.save(categoryVo.toDomain(id))
        }
    }

    private fun update(toBeUpdatedCategoriesData: List<Pair<CategoryData, Category>>) {
        toBeUpdatedCategoriesData.forEach { (data, category) ->
            category.update(
                label = data.label,
                parentId = data.parentId,
                order = data.order,
            )
            categoryRepository.update(category)
        }
    }

    private fun delete(toBeDeleteCategories: List<Category>) {
        toBeDeleteCategories.forEach { categoryToDelete ->
            categoryRepository.delete(categoryToDelete.id)
        }
    }

    private fun CategoryData.toDomain(id: CategoryId): Category = Category.newOne(
        id = id,
        label = label,
        parentId = parentId,
        order = order,
    )

    private fun List<CategoryData>.notContains(id: CategoryId): Boolean =
        this.none { categoryVo -> categoryVo.id == id }

    override fun findCategory(categoryId: CategoryId): Category? =
        categoryRepository.findById(categoryId)
}
