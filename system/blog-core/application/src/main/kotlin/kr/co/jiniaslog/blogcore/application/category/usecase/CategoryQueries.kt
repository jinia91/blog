package kr.co.jiniaslog.blogcore.application.category.usecase

import kr.co.jiniaslog.blogcore.domain.category.Category
import kr.co.jiniaslog.blogcore.domain.category.CategoryId

interface CategoryQueries {
    fun findCategory(categoryId: CategoryId): Category?
}
