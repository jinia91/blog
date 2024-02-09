package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@JvmInline
value class ThumbnailUrl(val url: String) : ValueObject {
    @PrePersist
    @PreUpdate
    override fun validate() {
        require(url.isEmpty()) {
            "url must be not empty"
        }
    }
}
