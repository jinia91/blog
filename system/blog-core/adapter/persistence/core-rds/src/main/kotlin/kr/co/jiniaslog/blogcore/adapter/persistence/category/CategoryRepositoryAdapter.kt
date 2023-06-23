package kr.co.jiniaslog.blogcore.adapter.persistence.category

import kr.co.jiniaslog.blogcore.domain.category.Category
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.category.CategoryIdGenerator
import kr.co.jiniaslog.blogcore.domain.category.CategoryRepository
import kr.co.jiniaslog.shared.core.domain.IdGenerator
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kotlin.jvm.optionals.getOrNull

class CategoryRepositoryAdapter(
    private val categoryJpaRepository: CategoryJpaRepository,
    private val categoryIdGenerator: IdGenerator,
) : CategoryRepository, CategoryIdGenerator {
    override fun generate(): CategoryId {
        return CategoryId(categoryIdGenerator.generate())
    }

    override fun save(newCategory: Category) {
        val categoryPm = newCategory.toPm().apply {
            newFlag = true
        }
        categoryJpaRepository.save(categoryPm)
        newCategory.syncAuditAfterPersist(
            categoryPm.createdDate!!,
            categoryPm.updatedDate!!,
        )
    }

    override fun update(category: Category) {
        val categoryPm = category.toPm().apply {
            newFlag = false
        }
        categoryJpaRepository.save(categoryPm)
        category.syncAuditAfterPersist(
            categoryPm.createdDate!!,
            categoryPm.updatedDate!!,
        )
    }

    override fun findById(categoryId: CategoryId): Category? {
        return categoryJpaRepository.findById(categoryId.value).getOrNull()?.toDomain()
    }

    override fun findByLabel(label: String): Category? {
        return categoryJpaRepository.findByLabel(label)?.toDomain()
    }

    override fun findAll(): List<Category> {
        return categoryJpaRepository.findAll().map { it.toDomain() }
    }

    override fun delete(categoryId: CategoryId) {
        val target = categoryJpaRepository.findById(categoryId.value)
            .getOrNull() ?: throw ResourceNotFoundException("$categoryId is not found")
        categoryJpaRepository.delete(target)
    }

    private fun Category.toPm(): CategoryPM = CategoryPM(
        id = this.id.value,
        label = this.label,
        parentId = this.parentId?.value,
        order = this.order,
        createdDate = this.createdDate,
        updatedDate = this.updatedDate,
    )

    private fun CategoryPM.toDomain(): Category = this.let {
        Category.Factory.from(
            id = CategoryId(id),
            label = label,
            parentId = parentId?.let { CategoryId(it) },
            order = order,
            createdAt = createdDate,
            updatedAt = updatedDate,
        )
    }
}
