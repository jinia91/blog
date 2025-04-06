package kr.co.jiniaslog.comment.domain

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class UserInfo(
    val userId: Long?,
    val userName: String,
    val password: String?,
) : ValueObject {

    init {
        validate()
    }

    override fun validate() {
        require(userId != null || password != null) { "유저 아이디나 비밀번호 는 필수입니다." }
    }
}
