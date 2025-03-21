package kr.co.jiniaslog.user.adapter.out.mysql.user

import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.User
import kr.co.jiniaslog.user.domain.user.UserId

@PersistenceAdapter
internal class UserRepositoryAdapter(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun findByEmail(email: Email): User? {
        return userJpaRepository.findByEmail(email.value)?.toDomain()
    }

    override fun findAll(): List<User> {
        return userJpaRepository.findAll().map { it.toDomain() }
    }

    override fun findAdminUsers(): List<User> {
        return userJpaRepository.findAllByRoles(Role.ADMIN.name).map { it.toDomain() }
    }

    override fun findById(id: UserId): User? {
        return userJpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun deleteById(id: UserId) {
        userJpaRepository.deleteById(id.value)
    }

    override fun save(entity: User): User {
        return userJpaRepository.save(entity.toJpaEntity()).toDomain()
    }
}

private fun User.toJpaEntity(): UserJpaEntity {
    return UserJpaEntity(
        id = this.entityId.value,
        email = this.email.value,
        nickName = this.nickName.value,
        roles = this.roles.joinToString(",") { it.name },
        picUrl = this.picUrl?.value,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}
