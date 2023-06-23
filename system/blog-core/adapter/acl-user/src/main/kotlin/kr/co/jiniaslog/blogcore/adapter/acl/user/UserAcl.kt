package kr.co.jiniaslog.blogcore.adapter.acl.user

import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.blogcore.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.context.AntiCorruptLayer
import kr.co.jiniaslog.user.application.UserServiceStub

@AntiCorruptLayer
internal class UserAcl(
    private val userServiceStub: UserServiceStub,
) : UserServiceClient {
    override fun doesUserExist(userId: UserId): Boolean = true
}
