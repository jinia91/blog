package kr.co.jiniaslog.comment.adapter.outbound.user

import kr.co.jiniaslog.comment.domain.UserInfo
import kr.co.jiniaslog.comment.outbound.UserService
import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import kr.co.jiniaslog.user.adapter.inbound.acl.UserAclInboundAdapter

@CustomComponent
class UserServiceInComment(
    private val userQueries: UserAclInboundAdapter,
) : UserService {
    override fun getUserInfo(userId: Long): UserInfo {
        return userQueries.retrieveUserInfo(userId).let {
            UserInfo(
                userId = it.id,
                userName = it.name,
                password = null,
            )
        }
    }
}
