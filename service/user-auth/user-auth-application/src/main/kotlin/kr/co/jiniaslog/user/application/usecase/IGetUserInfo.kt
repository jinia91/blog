package kr.co.jiniaslog.user.application.usecase

import kr.co.jiniaslog.user.domain.user.UserId

interface IGetUserInfo {
    fun handle(query: Query): Info

    data class Query(
        val userId: UserId,
    )

    data class Info(
        val id: UserId,
        val name: String,
        val profileImageUrl: String?,
    )
}
