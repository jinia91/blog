package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.Embeddable
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.io.Serializable
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class Tagging(
    val tagId: TagId,
) : ValueObject, Serializable {
    @PrePersist
    @PreUpdate
    override fun validate() {}
}
