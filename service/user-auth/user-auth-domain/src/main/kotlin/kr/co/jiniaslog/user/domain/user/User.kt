package kr.co.jiniaslog.user.domain.user

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils
import kr.co.jiniaslog.shared.core.domain.vo.Url
import java.time.LocalDateTime

class User private constructor(
    id: UserId,
    nickName: NickName,
    roles: Set<Role>,
    email: Email,
    picUrl: Url?,
) : AggregateRoot<UserId>() {
    override val entityId: UserId = id

    var nickName: NickName = nickName
        private set

    var roles = roles
        private set

    var email: Email = email
        private set

    var picUrl: Url? = picUrl
        private set

    fun update(nickName: NickName, picUrl: Url?) {
        this.nickName = nickName
        this.picUrl = picUrl
    }

    companion object {
        fun newOne(
            nickName: NickName,
            email: Email,
            picUrl: Url?
        ): User {
            return User(
                id = UserId(IdUtils.generate()),
                nickName = nickName,
                roles = setOf(Role.USER),
                email = email,
                picUrl = picUrl,
            )
        }

        fun from(
            id: UserId,
            nickName: NickName,
            roles: Set<Role>,
            email: Email,
            picUrl: Url?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): User {
            return User(
                id = id,
                nickName = nickName,
                roles = roles,
                email = email,
                picUrl = picUrl,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
