package kr.co.jiniaslog.article.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.co.jiniaslog.article.application.infra.TransactionHandler
import kr.co.jiniaslog.article.application.port.ArticleIdGenerator
import kr.co.jiniaslog.article.application.port.ArticleStore
import kr.co.jiniaslog.article.application.usecase.ArticlePostCommand
import kr.co.jiniaslog.article.domain.Article
import kr.co.jiniaslog.article.domain.ArticleFactory
import kr.co.jiniaslog.article.domain.ArticleId
import kr.co.jiniaslog.article.domain.CategoryId
import kr.co.jiniaslog.article.domain.TagId
import kr.co.jiniaslog.article.domain.WriterId

internal class ArticlePostUseCaseTests : BehaviorSpec() {
    private val articleStore: ArticleStore = mockk(relaxed = true)
    private val transactionHandler: TransactionHandler = mockk(relaxed = true)
    private val articleIdGenerator: ArticleIdGenerator = mockk(relaxed = true)
    private val articleFactory: ArticleFactory = mockk(relaxed = true)

    private val sut = ArticleService(articleStore, articleIdGenerator, transactionHandler, articleFactory)

    init {
        Given("다음과 같은 command가 주어질 때") {
            val tags = setOf(TagId(1), TagId(2), TagId(3))
            val command = ArticlePostCommand(
                writerId = WriterId(1),
                title = "title",
                content = "content",
                thumbnailUrl = "thumbnailUrl",
                categoryId = CategoryId(1),
                tags = tags,
            )
            val articleId = ArticleId(1)

            every { articleIdGenerator.generate() } returns articleId
            val mockArticleDomainEntity = mockk<Article> {
                every { id }.returns(articleId)
            }
            every {
                articleFactory.newOne(
                    writerId = command.writerId,
                    title = command.title,
                    content = command.content,
                    thumbnailUrl = command.thumbnailUrl,
                    categoryId = command.categoryId,
                    id = articleId,
                    tags = tags,
                )
            }.returns(mockArticleDomainEntity)

            every { transactionHandler.runInReadCommittedTransaction<Article>(any()) } returns mockArticleDomainEntity

            When("sut.postArticle(command)") {
                val result = sut.postArticle(command)
                Then("정상 저장된다") {
                    result shouldBe articleId.value
                }
            }
        }
    }
}
