package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.shared.core.domain.ValueObject

@JvmInline
value class CategoryName(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.length in 1..20) { "category name must be between 1 and 20 characters" }
    }
}

@JvmInline
value class CategoryId(val value: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value > 0) { "category id must be positive" }
    }
}
