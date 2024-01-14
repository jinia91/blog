package kr.co.jiniaslog.user.domain.user

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils
import kr.co.jiniaslog.user.domain.auth.provider.ProviderUserInfo
import java.time.LocalDateTime

class User private constructor(
    id: UserId,
    nickName: NickName,
    roles: Set<Role>,
    email: Email,
) : AggregateRoot<UserId>() {
    override val id: UserId = id

    var nickName: NickName = nickName
        private set

    var roles = roles
        private set

    var email: Email = email
        private set

    fun refreshWith(userInfo: ProviderUserInfo) {
        takeIf { this.nickName != userInfo.nickName }
            .let { this.nickName = userInfo.nickName }
    }

    companion object {
        fun newOne(userInfo: ProviderUserInfo): User {
            return User(
                id = UserId(IdUtils.generate()),
                nickName = userInfo.nickName,
                roles = setOf(Role.USER),
                email = userInfo.email,
            )
        }

        fun from(
            id: UserId,
            nickName: NickName,
            roles: Set<Role>,
            email: Email,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): User {
            return User(
                id = id,
                nickName = nickName,
                roles = roles,
                email = email,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
