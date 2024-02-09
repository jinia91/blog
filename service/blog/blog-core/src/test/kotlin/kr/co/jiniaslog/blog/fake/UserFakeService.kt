package kr.co.jiniaslog.blog.fake

import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.blog.outbound.UserService

class UserFakeService : UserService {
    private val users = mutableMapOf<UserId, DummyUser>()

    override fun isExistUser(id: UserId): Boolean {
        return users.containsKey(id)
    }

    fun addDummy(id: UserId) : UserId {
        users[id] = DummyUser(id)
        return id
    }
}

data class DummyUser(
    val id: UserId,
)
