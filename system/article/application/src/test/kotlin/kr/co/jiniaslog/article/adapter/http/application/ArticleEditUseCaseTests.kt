package kr.co.jiniaslog.article.adapter.http.application

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.article.application.ArticleService
import kr.co.jiniaslog.article.application.infra.TransactionHandler
import kr.co.jiniaslog.article.application.port.ArticleIdGenerator
import kr.co.jiniaslog.article.application.port.ArticleRepository
import kr.co.jiniaslog.article.application.port.UserServiceClient
import kr.co.jiniaslog.article.application.usecase.ArticleEditCommand
import kr.co.jiniaslog.article.domain.Article
import kr.co.jiniaslog.article.domain.ArticleFactory
import kr.co.jiniaslog.article.domain.ArticleId
import kr.co.jiniaslog.article.domain.CategoryId
import kr.co.jiniaslog.article.domain.TagId
import kr.co.jiniaslog.article.domain.UserId

internal class ArticleEditUseCaseTests : BehaviorSpec() {
    private val articleRepository: ArticleRepository = mockk(relaxed = true)
    private val transactionHandler: TransactionHandler = mockk(relaxed = true)
    private val articleIdGenerator: ArticleIdGenerator = mockk(relaxed = true)
    private val articleFactory: ArticleFactory = mockk(relaxed = true)
    private val userAcl: UserServiceClient = mockk(relaxed = true)

    private val sut = ArticleService(articleRepository, articleIdGenerator, transactionHandler, articleFactory, userAcl)

    init {
        Given("다음과 같은 command가 주어질 때") {
            val tags = setOf(TagId(1), TagId(2), TagId(3))
            val articleId = ArticleId(1)
            val command = ArticleEditCommand(
                articleId = articleId,
                title = "edit title",
                content = "edit content",
                thumbnailUrl = "thumbnailUrl",
                categoryId = CategoryId(1),
                tags = tags,
            )
            val mockArticle = buildMockArticle(articleId, tags)

            every { userAcl.findUserById(articleId.value) }.returns(UserId(1))

            every { articleRepository.findById(articleId) } returns mockArticle

            When("sut.editArticle(command)") {
                sut.editArticle(command)
                Then("아티클이 수정된다.") {
                    verify(exactly = 1) { transactionHandler.runInReadCommittedTransaction(any()) }
                }
            }
        }
    }

    private fun buildMockArticle(
        articleId: ArticleId,
        tags: Set<TagId>,
    ) = Article(
        id = articleId,
        userId = UserId(1),
        title = "title",
        content = "content",
        thumbnailUrl = "thumbnailUrl",
        categoryId = CategoryId(1),
        tags = tags,
        hit = 0,
    )
}
