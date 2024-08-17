package kr.co.jiniaslog.user.application.usecase

import kr.co.jiniaslog.user.domain.user.UserId

interface ILogOut {
    fun handle(command: Command): Info

    data class Command(
        val userId: UserId,
    )

    class Info()
}
