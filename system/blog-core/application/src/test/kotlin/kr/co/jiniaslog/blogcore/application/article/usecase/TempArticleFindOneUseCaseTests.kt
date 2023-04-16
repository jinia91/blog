package kr.co.jiniaslog.blogcore.application.article.usecase

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleId
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.UserId
import java.util.function.Supplier

internal class TempArticleFindOneUseCaseTests : BehaviorSpec() {

    private val transactionHandler: TransactionHandler = mockk()
    private val tempArticleRepository: TempArticleRepository = mockk(relaxed = true)
    private val sut = TempArticleFindOneUseCaseInteractor(transactionHandler, tempArticleRepository)

    init {
        every<Unit> { transactionHandler.runInReadCommittedTransaction(any()) } answers {
            val supplier = arg<Supplier<*>>(0)
            supplier.get()
        }

        Given("임시 아티클이 이미 존재할 때") {
            every { tempArticleRepository.getTemp(TempArticleId.getDefault()) }
                .returns(
                    TempArticle.Factory.from(
                        writerId = UserId(value = 8525),
                        title = null,
                        content = null,
                        thumbnailUrl = null,
                        categoryId = null,
                    ),
                )

            When("임시 아티클 조회를 하면") {
                val tempArticle = sut.findOne()
                Then("임시 아티클이 조회된다.") {
                    tempArticle shouldNotBe null
                }
            }
        }
    }
}
