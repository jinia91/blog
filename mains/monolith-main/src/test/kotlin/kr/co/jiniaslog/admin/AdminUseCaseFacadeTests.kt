package kr.co.jiniaslog.admin

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.admin.application.CreateAndLoginMockUser
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.domain.user.Role
import kr.co.jiniaslog.user.domain.user.UserId
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AdminUseCaseFacadeTests : TestContainerAbstractSkeleton() {
    @Nested
    inner class `목 유저 생성 테스트` {
        @Autowired
        lateinit var createAndLoginMockUser: CreateAndLoginMockUser

        @Autowired
        lateinit var userRepository: UserRepository

        @Test
        fun `어드민 유저를 생성할 수 있다`() {
            // given
            val command = CreateAndLoginMockUser.Command(Role.ADMIN, UserId(1L))

            // when
            val info = createAndLoginMockUser.handle(command)

            // then
            val user = userRepository.findById(info.id)
            user shouldNotBe null
            user!!.roles.first() shouldBe Role.ADMIN
        }

        @Test
        fun `일반 유저를 생성할 수 있다`() {
            // given
            val command = CreateAndLoginMockUser.Command(Role.USER, UserId(2L))

            // when
            val info = createAndLoginMockUser.handle(command)

            // then
            val user = userRepository.findById(info.id)
            user shouldNotBe null
            user!!.roles.first() shouldBe Role.USER
        }
    }
}
