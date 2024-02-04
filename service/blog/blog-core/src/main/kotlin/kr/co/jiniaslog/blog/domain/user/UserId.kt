package kr.co.jiniaslog.blog.domain.user

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class UserId(val value: Long) : ValueObject {
    override fun validate() {
        require(value > 0) { "id must be positive" }
    }
}
