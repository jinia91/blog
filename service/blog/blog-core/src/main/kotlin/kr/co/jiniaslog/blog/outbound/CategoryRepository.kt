package kr.co.jiniaslog.blog.outbound

import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.shared.core.domain.Repository

interface CategoryRepository : Repository<Category, CategoryId> {
    fun findAll(): List<Category>

    fun deleteAll(list: List<Category>)

    fun saveAll(list: List<Category>)
}
