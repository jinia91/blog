package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable
import java.time.LocalDateTime

@Embeddable
data class Tagging(
    val tagId: TagId,
    val createdAt: LocalDateTime? = LocalDateTime.now()
) : ValueObject, Serializable {
    init {
        validate()
    }
    override fun validate() {}
}
