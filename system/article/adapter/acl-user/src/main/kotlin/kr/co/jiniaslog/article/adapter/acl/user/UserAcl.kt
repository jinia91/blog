package kr.co.jiniaslog.article.adapter.acl.user

import kr.co.jiniaslog.article.application.port.UserServiceClient
import kr.co.jiniaslog.article.domain.UserId
import kr.co.jiniaslog.lib.context.AntiCorruptLayer
import kr.co.jiniaslog.user.application.UserServiceStub

@AntiCorruptLayer
class UserAcl(
    private val userServiceStub: UserServiceStub,
) : UserServiceClient {
    override fun isAdmin(id: Long) = UserId(userServiceStub.stub(id).id) == UserId(1)
}
