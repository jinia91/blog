package kr.co.jiniaslog.blog.domain.tag

import jakarta.persistence.Embeddable
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.io.Serializable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class TagId(val id: Long) : ValueObject, Serializable {
    @PreUpdate
    @PrePersist
    override fun validate() {
        require(id > 0) {
            "id must be positive"
        }
    }
}
