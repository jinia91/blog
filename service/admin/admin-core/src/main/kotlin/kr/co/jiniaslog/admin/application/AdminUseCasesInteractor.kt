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
    override fun handle(command: CreateMockUser.Command): CreateMockUser.Info {
        userRepository.save(
            User.newOne(
                nickName = NickName("Test User"),
                email = Email("test@Test.com")
            )
        )
        return CreateMockUser.Info()
    }
}
