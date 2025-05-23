package kr.co.jiniaslog.blog.domain.tag

import jakarta.persistence.Embeddable
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class TagId(val id: Long) : ValueObject, Serializable {
    init {
        validate()
    }

    @PreUpdate
    @PrePersist
    override fun validate() {
        require(id > 0) { "태그 id는 양수여야 합니다" }
    }
}
