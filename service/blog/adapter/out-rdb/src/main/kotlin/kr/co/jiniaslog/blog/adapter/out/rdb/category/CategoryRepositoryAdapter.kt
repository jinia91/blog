package kr.co.jiniaslog.blog.adapter.out.rdb.category

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryRepository
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryAdapter(
    private val categoryRdbRepository: CategoryRdbRepository,
) : CategoryRepository {
    override suspend fun nextId(): CategoryId {
        return CategoryId(IdUtils.generate())
    }

    override suspend fun findById(id: CategoryId): Category? {
        return categoryRdbRepository.findById(id.value)?.toDomain()
    }

    override suspend fun findAll(): List<Category> {
        return categoryRdbRepository.findAll().map { it.toDomain() }.toList()
    }

    override suspend fun deleteById(id: CategoryId) {
        categoryRdbRepository.deleteById(id.value)
    }

    override suspend fun save(entity: Category): Category {
        return categoryRdbRepository.save(entity.toPM()).toDomain()
    }
}
