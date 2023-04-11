package kr.co.jiniaslog.user.application

import kr.co.jiniaslog.shared.core.context.UseCaseInteractor
import kr.co.jiniaslog.user.domain.User

@UseCaseInteractor
class UserServiceStub {

    fun stub(id: Long): User {
        return User(
            id = id,
            name = "name",
            email = "email",
            password = "password",
            roles = setOf("USER"),
        )
    }
}
