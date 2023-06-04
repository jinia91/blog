package kr.co.jiniaslog.blogcore.domain.category

interface CategoryRepository {
    fun save(newCategory: Category)

    fun findById(categoryId: CategoryId): Category?

    fun findByLabel(label: String): Category?

    fun findAll(): List<Category>

    fun delete(categoryId: CategoryId)
}
