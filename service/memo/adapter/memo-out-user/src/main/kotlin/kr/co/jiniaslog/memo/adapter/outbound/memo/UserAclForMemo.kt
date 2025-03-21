package kr.co.jiniaslog.memo.adapter.outbound.memo

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.user.UserService
import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import kr.co.jiniaslog.user.adapter.inbound.acl.UserAclInboundAdapter

@CustomComponent
class UserAclForMemo(
    private val userQueries: UserAclInboundAdapter,
) : UserService {
    override fun retrieveAdminUserIds(): List<AuthorId> {
        return userQueries.retrieveAdminUserIds().map { AuthorId(it) }
    }
}
