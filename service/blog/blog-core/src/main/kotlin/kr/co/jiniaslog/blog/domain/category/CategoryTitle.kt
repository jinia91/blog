package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class CategoryTitle(val value: String) : ValueObject {
    init {
        validate()
    }

    override fun validate() {
        require(value.length <= 20) { "카테고리 이름은 20자를 넘을 수 없습니다." }
        require(value.isNotEmpty()) { "카테고리 이름은 필수입니다." }
    }
}
