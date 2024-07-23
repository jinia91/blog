package kr.co.jiniaslog.admin.application

import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId

interface CreateAndLoginMockUser {
    fun handle(command: Command): Info

    data class Command(val role: Role, val userId: UserId)

    class Info(val id: UserId, val role: Role)
}
