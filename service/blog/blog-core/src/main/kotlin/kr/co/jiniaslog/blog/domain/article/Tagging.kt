package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class Tagging(
    val tagId: TagId,
) : ValueObject {
    override fun validate() {}
}
