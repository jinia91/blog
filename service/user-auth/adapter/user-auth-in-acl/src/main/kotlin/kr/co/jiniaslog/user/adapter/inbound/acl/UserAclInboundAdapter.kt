package kr.co.jiniaslog.user.adapter.inbound.acl

import kr.co.jiniaslog.user.application.usecase.ICheckUserExisted
import kr.co.jiniaslog.user.application.usecase.IRetrieveAdminUserIds
import kr.co.jiniaslog.user.application.usecase.UseCasesUserAuthFacade
import kr.co.jiniaslog.user.domain.user.UserId
import org.springframework.stereotype.Controller

@Controller
class UserAclInboundAdapter(
    private val userQueries: UseCasesUserAuthFacade,
) {
    fun isExistUser(id: Long): Boolean {
        return userQueries.handle(ICheckUserExisted.Command(UserId(id)))
    }

    fun retrieveAdminUserIds(): List<Long> {
        return userQueries.handle(IRetrieveAdminUserIds.Query()).ids.map { it.value }
    }
}
