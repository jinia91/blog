package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class ThumbnailUrl(val url: String) : ValueObject {
    override fun validate() {
        require(url.isNotEmpty()) {
            "url must be not empty"
        }
    }
}
