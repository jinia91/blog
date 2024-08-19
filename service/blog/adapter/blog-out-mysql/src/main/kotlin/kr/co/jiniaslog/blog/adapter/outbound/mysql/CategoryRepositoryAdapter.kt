package kr.co.jiniaslog.blog.adapter.outbound.mysql

import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.outbound.CategoryRepository
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryJpaRepository : JpaRepository<Category, CategoryId>

@org.springframework.stereotype.Repository
class CategoryRepositoryAdapter(
    private val categoryJpaRepository: CategoryJpaRepository,
) : CategoryRepository {
    override fun save(entity: Category): Category {
        return categoryJpaRepository.save(entity)
    }

    override fun findAll(): List<Category> {
        return categoryJpaRepository.findAll()
    }

    override fun deleteAll(list: List<Category>) {
        return categoryJpaRepository.deleteAll(list)
    }

    override fun saveAll(list: List<Category>) {
        // persistable isNew의 동작 문제로 연관관계 매핑된 객체 저장시 cascade 만 이용하여 저장하도록 조절
        // 최상위 부모만 저장하고 나머지는 cascade로 저장토록 한다
        val target = list.filter { it.parent == null }
        categoryJpaRepository.saveAll(target)
    }

    override fun findById(id: CategoryId): Category? {
        return categoryJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: CategoryId) {
        categoryJpaRepository.deleteById(id)
    }
}
