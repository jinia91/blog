package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class CategoryId(val id: Long) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(id > 0) {
            "id must be positive"
        }
    }
}
