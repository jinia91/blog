package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.blog.domain.category.CategoryId

interface CategoryIdGenerator {
    fun generate(): CategoryId
}
