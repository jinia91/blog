package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.Embeddable
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class UserLike(
    val userId: UserId,
) : ValueObject {
    @PrePersist
    @PreUpdate
    override fun validate() {}
}
