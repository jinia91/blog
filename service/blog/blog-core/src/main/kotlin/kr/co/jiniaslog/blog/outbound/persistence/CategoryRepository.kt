package kr.co.jiniaslog.blog.outbound.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.QCategory.category
import kr.co.jiniaslog.shared.core.domain.Repository
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : Repository<Category, CategoryId> {
    fun findAll(): List<Category>

    fun deleteAll(list: List<Category>)

    fun saveAll(list: List<Category>): List<Category>
}

interface CategoryJpaRepository : JpaRepository<Category, CategoryId>

@org.springframework.stereotype.Repository
class CategoryRepositoryAdapter(
    private val categoryJpaRepository: CategoryJpaRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : CategoryRepository {
    override fun save(entity: Category): Category {
        return categoryJpaRepository.save(entity)
    }

    override fun findAll(): List<Category> {
        return categoryJpaRepository.findAll()
    }

    override fun deleteAll(list: List<Category>) {
//        return categoryJpaRepository.deleteAll(list)
        list.forEach {
            deleteById(it.entityId)
        }
    }

    override fun saveAll(list: List<Category>): List<Category> {
        return categoryJpaRepository.saveAll(list)
    }

    override fun findById(id: CategoryId): Category? {
        return categoryJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: CategoryId) {
//        categoryJpaRepository.deleteById(id)
        jpaQueryFactory.delete(category)
            .where(category.entityId.eq(id))
            .execute()
    }
}
