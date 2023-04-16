package kr.co.jiniaslog.blogcore.application.article.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.UserId
import kr.co.jiniaslog.blogcore.domain.article.UserServiceClient
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import java.util.function.Supplier

internal class TempArticlePostUseCaseTests : BehaviorSpec() {

    private val transactionHandler: TransactionHandler = mockk()
    private val tempArticleRepository: TempArticleRepository = mockk(relaxed = true)
    private val userServiceClient: UserServiceClient = mockk(relaxed = true)
    private val sut = TempArticlePostUseCaseInteractor(transactionHandler, tempArticleRepository, userServiceClient)

    init {
        every<Unit> { transactionHandler.runInReadCommittedTransaction(any()) } answers {
            val supplier = arg<Supplier<*>>(0)
            supplier.get()
        }

        Given("임시 아티클 등록 명령이 주어질때") {
            val command = TempArticlePostCommand(
                writerId = UserId(value = 6343),
                title = null,
                content = null,
                thumbnailUrl = null,
                categoryId = null,
            )

            and("validation을 통과하지 못하면") {
                every { userServiceClient.userExists(command.writerId) } returns false
                When("임시 아티클 등록 명령을 실행하면") {
                    Then("예외가 발생한다") {
                        shouldThrow<ResourceNotFoundException> {
                            sut.post(command)
                        }
                    }
                }
            }

            and("저자 아이디가 존재한다면") {
                every { userServiceClient.userExists(command.writerId) } returns true
                When("임시 아티클 등록 명령을 실행하면") {
                    sut.post(command)
                    Then("정상 저장 된다.") {
                        verify(exactly = 1) { tempArticleRepository.save(any()) }
                    }
                }
            }
        }
    }
}
