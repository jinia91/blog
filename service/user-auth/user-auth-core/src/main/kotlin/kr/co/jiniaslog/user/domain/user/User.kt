package kr.co.jiniaslog.user.domain.user

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils
import kr.co.jiniaslog.user.domain.auth.ProviderUserInfo
import java.time.LocalDateTime

class User private constructor(
    id: UserId,
    nickName: String,
    role: Role,
    email: String,
) : AggregateRoot<UserId>() {
    override val id: UserId = id

    var nickName: String = nickName
        private set

    var role = role
        private set

    var email: String = email
        private set

    fun renewNickName(providerUserInfo: ProviderUserInfo) {
        this.nickName = providerUserInfo.nickName
    }

    companion object {
        fun newOne(providerUserInfo: ProviderUserInfo): User {
            return User(
                id = UserId(IdUtils.generate()),
                nickName = providerUserInfo.nickName,
                role = Role.USER,
                email = providerUserInfo.email,
            )
        }

        fun from(
            id: UserId,
            nickName: String,
            role: Role,
            email: String,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): User {
            return User(
                id = id,
                nickName = nickName,
                role = role,
                email = email,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
