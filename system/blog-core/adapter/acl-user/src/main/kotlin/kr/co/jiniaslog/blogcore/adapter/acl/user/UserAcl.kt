package kr.co.jiniaslog.blogcore.adapter.acl.user

import kr.co.jiniaslog.blogcore.domain.article.UserId
import kr.co.jiniaslog.blogcore.domain.article.UserServiceClient
import kr.co.jiniaslog.shared.core.context.AntiCorruptLayer
import kr.co.jiniaslog.user.application.UserServiceStub

@AntiCorruptLayer
internal class UserAcl(
    private val userServiceStub: UserServiceStub,
) : UserServiceClient {
    override fun userExists(userId: UserId): Boolean = true
}
