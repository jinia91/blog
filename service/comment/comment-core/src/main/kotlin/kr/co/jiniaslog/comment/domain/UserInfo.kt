package kr.co.jiniaslog.comment.domain

import jakarta.persistence.Embeddable

@Embeddable
data class UserInfo(
    val userId: Long?,
    val userName: String,
    val password: String?,
)
