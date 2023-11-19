package kr.co.jiniaslog.blog.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleStatus
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.articleview.ArticleView
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.writer.WriterId
import kr.co.jiniaslog.blog.fakeArticleRepository
import kr.co.jiniaslog.blog.fakeArticleViewRepository
import kr.co.jiniaslog.blog.fakeCategoryRepository
import kr.co.jiniaslog.blog.fakeWriterProvider
import kr.co.jiniaslog.blog.usecase.ArticleViewUpsertUseCase.ArticleViewUpsertCommand
import kr.co.jiniaslog.shared.CustomBehaviorSpec
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

class ArticleViewUseCasesTests : CustomBehaviorSpec() {
    private var articleRepository = fakeArticleRepository()
    private var articleViewRepository = fakeArticleViewRepository()
    private var writerProvider = fakeWriterProvider()
    private var categoryRepository = fakeCategoryRepository()
    private var sut =
        ArticleViewUseCasesImpl(
            articleRepository = articleRepository,
            articleViewRepository = articleViewRepository,
            writerProvider = writerProvider,
            categoryRepository = categoryRepository,
            transactionHandler = testTransactionHandler,
        )

    override suspend fun afterEach(
        testCase: TestCase,
        result: TestResult,
    ) {
        tearDown()
        super.afterEach(testCase, result)
    }

    private fun tearDown() {
        articleRepository = fakeArticleRepository()
        articleViewRepository = fakeArticleViewRepository()
        writerProvider = fakeWriterProvider()
        categoryRepository = fakeCategoryRepository()
    }

    init {
        Given("유효한 아티클이 있고") {
            val articleId = ArticleId(IdUtils.generate())
            val article =
                Article.init(
                    id = articleId,
                    writerId = WriterId(1),
                )
            article.commit(
                title = ArticleTitle("title"),
                newContent = ArticleContent("content"),
                null,
                null,
            )
            articleRepository.save(
                article,
            )
            And("아티클과 카테고리가 다른 아티클 커맨드가 있고") {
                val command =
                    ArticleViewUpsertCommand(
                        articleId = ArticleId(IdUtils.generate()),
                        writerId = WriterId(1),
                        categoryId = CategoryId(IdUtils.generate()),
                        headVersion = article.latestCommit.id,
                    )
                When("아티클 뷰 생성 usecase를 실행하면") {
                    Then("아티클 뷰 생성이 실패한다") {
                        shouldThrow<IllegalArgumentException> {
                            sut.upsert(command)
                        }
                    }
                }
            }

            And("유효한 아티클 뷰 생성 커맨드가 있고") {
                val command =
                    ArticleViewUpsertCommand(
                        articleId = articleId,
                        writerId = WriterId(1),
                        categoryId = null,
                        headVersion = article.latestCommit.id,
                    )

                And("아티클 뷰가 없다면") {
                    When("아티클 뷰 생성 usecase를 실행하면") {
                        val info = sut.upsert(command)
                        Then("아티클 뷰 생성이 실행된다") {
                            info.articleId shouldBe command.articleId
                            articleViewRepository.findById(info.articleId) shouldNotBe null
                        }
                    }
                }
                And("아티클 뷰가 있다면") {
                    val articleView =
                        articleViewRepository.save(
                            ArticleView.from(
                                id = articleId,
                                title = null,
                                writer = writerProvider.getWriter(WriterId(1))!!.name,
                                thumbnailUrl = null,
                                categoryName = null,
                                content = null,
                                status = ArticleStatus.PUBLISHED,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now(),
                            ),
                        )
                    When("아티클 뷰 생성 usecase를 실행하면") {
                        val info = sut.upsert(command)
                        Then("아티클 뷰 생성이 실행된다") {
                            info.articleId shouldBe command.articleId
                            articleViewRepository.findById(info.articleId) shouldNotBe null
                        }
                    }
                }
            }
        }
    }
}
