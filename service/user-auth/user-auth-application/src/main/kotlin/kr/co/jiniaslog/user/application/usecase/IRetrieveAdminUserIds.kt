package kr.co.jiniaslog.user.application.usecase

import kr.co.jiniaslog.user.domain.user.UserId

interface IRetrieveAdminUserIds {
    fun handle(query: Query): Info

    class Query()
    data class Info(val ids: List<UserId>)
}
