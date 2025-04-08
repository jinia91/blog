package kr.co.jiniaslog.user.adapter.inbound.acl

import kr.co.jiniaslog.user.application.usecase.ICheckUserExisted
import kr.co.jiniaslog.user.application.usecase.IGetUserInfo
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

    data class UserInfo(
        val id: Long,
        val name: String,
        val profileImageUrl: String?,
    )

    fun retrieveUserInfo(userId: Long): UserInfo {
        val user = userQueries.handle(IGetUserInfo.Query(UserId(userId)))
        return UserInfo(
            id = user.id.value,
            name = user.name,
            profileImageUrl = user.profileImageUrl,
        )
    }
}
