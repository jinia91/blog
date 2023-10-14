package kr.co.jiniaslog.blog.application.usecase

import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId

interface CategoryQueries {
    fun findCategory(categoryId: CategoryId): Category?
}
