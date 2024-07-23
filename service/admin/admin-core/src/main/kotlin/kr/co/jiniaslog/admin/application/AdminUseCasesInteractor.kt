package kr.co.jiniaslog.admin.application

import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.User
import org.springframework.context.annotation.Profile

@UseCaseInteractor
@Profile("!prod")
class AdminUseCasesInteractor(
    private val userRepository: UserRepository
) : AdminUseCases {
    override fun handle(command: CreateAndLoginMockUser.Command): CreateAndLoginMockUser.Info {
        val user = User.from(
            id = command.userId,
            nickName = NickName("Test User"),
            email = Email("test${command.userId.value}@Test.com"),
            roles = setOf(command.role),
            createdAt = null,
            updatedAt = null
        )
        userRepository.save(
            user
        )
        return CreateAndLoginMockUser.Info(user.entityId, user.roles.first())
    }
}
