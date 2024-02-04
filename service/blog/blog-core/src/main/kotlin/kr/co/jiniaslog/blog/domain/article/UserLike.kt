package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class UserLike(
    val userId: UserId,
) : ValueObject {
    override fun validate() {}
}
