package kr.co.jiniaslog.blogcore.application.article.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.blogcore.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import java.util.function.Supplier

internal class DraftArticlePostUseCaseTests : BehaviorSpec() {

    private val transactionHandler: TransactionHandler = mockk()
    private val articleRepository: ArticleRepository = mockk(relaxed = true)
    private val userServiceClient: UserServiceClient = mockk()
    private val articleIdGenerator: ArticleIdGenerator = mockk()
    private val sut = DraftArticlePostUseCaseInteractor(transactionHandler, articleIdGenerator, articleRepository, userServiceClient)

    init {
        every<Unit> { transactionHandler.runInReadCommittedTransaction(any()) } answers {
            val supplier = arg<Supplier<*>>(0)
            supplier.get()
        }

        Given("아티클 초안 등록 명령이 주어질때") {
            val command = DraftArticlePostCommand(
                writerId = UserId(value = 6343),
                title = "vis",
                content = "sem",
                thumbnailUrl = null,
                categoryId = null,
                tags = setOf(),
            )

            and("저자 아이디가 존재하지 않으면") {
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
                every { articleIdGenerator.generate() } returns ArticleId(1)
                When("아티클 초안 등록 명령을 실행하면") {
                    sut.post(command)
                    Then("정상 저장 된다.") {
                        verify(exactly = 1) { articleRepository.save(any()) }
                    }
                }
            }
        }
    }
}
