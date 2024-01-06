package kr.co.jiniaslog.user.adapter.out.mysql.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.co.jiniaslog.user.adapter.out.mysql.AbstractPersistenceModel
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.User
import kr.co.jiniaslog.user.domain.user.UserId
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class UserJpaEntity(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    override val id: Long,
    @Column(name = "email", nullable = false, unique = true)
    var email: String,
    @Column(name = "password", nullable = false)
    var nickName: String,
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role,
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
) : AbstractPersistenceModel(createdAt, updatedAt) {
    fun toDomain(): User {
        return User.from(
            id = UserId(this.id),
            nickName = this.nickName,
            role = this.role,
            email = this.email,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )
    }
}
