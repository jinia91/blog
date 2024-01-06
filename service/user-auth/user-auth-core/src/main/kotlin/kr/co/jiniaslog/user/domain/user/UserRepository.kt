package kr.co.jiniaslog.user.domain.user

import kr.co.jiniaslog.shared.core.domain.Repository

interface UserRepository : Repository<User, UserId> {
    fun findByEmail(email: String): User?
}
