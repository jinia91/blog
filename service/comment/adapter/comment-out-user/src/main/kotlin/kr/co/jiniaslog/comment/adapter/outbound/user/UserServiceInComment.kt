package kr.co.jiniaslog.comment.adapter.outbound.user

import kr.co.jiniaslog.comment.domain.AuthorInfo
import kr.co.jiniaslog.comment.outbound.UserService
import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import kr.co.jiniaslog.user.adapter.inbound.acl.UserAclInboundAdapter

@CustomComponent
class UserServiceInComment(
    private val userQueries: UserAclInboundAdapter,
) : UserService {
    override fun getUserInfo(userId: Long): AuthorInfo {
        return userQueries.retrieveUserInfo(userId).let {
            AuthorInfo(
                authorId = it.id,
                authorName = it.name,
                password = null,
                profileImageUrl = it.profileImageUrl,
            )
        }
    }

    override fun isAdmin(userId: Long): Boolean {
        val ids = userQueries.retrieveAdminUserIds()
        return ids.contains(userId)
    }
}
