package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class ThumbnailUrl(val value: String) : ValueObject {
    override fun validate() {
        require(value.isEmpty()) {
            "url must be not empty"
        }
    }
}
