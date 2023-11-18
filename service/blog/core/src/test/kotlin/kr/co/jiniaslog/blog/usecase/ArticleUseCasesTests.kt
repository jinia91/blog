package kr.co.jiniaslog.blog.usecase

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.article.WriterId
import kr.co.jiniaslog.shared.CustomBehaviorSpec
import kr.co.jiniaslog.shared.core.domain.FetchMode
import kr.co.jiniaslog.shared.core.domain.IdUtils

class ArticleUseCasesTests : CustomBehaviorSpec() {
    private var fakeRepo = fakeArticleRepository()
    private val sut =
        ArticleUseCasesImpl(
            articleRepository = fakeRepo,
            transactionHandler = testTransactionHandler,
        )

    override suspend fun afterEach(
        testCase: TestCase,
        result: TestResult,
    ) {
        fakeRepo = fakeArticleRepository()
        super.afterEach(testCase, result)
    }

    init {
        Given("유효한 아티클 초기화 command가 있고") {
            val command =
                ArticleInitCommandUseCase.ArticleInitCommand(
                    writerId = WriterId(1),
                )
            When("아티클 초기화 usecase를 실행하면") {
                val info = sut.init(command)

                Then("아티클 초기화가 실행된다") {
                    info.articleId shouldBe fakeRepo.findById(info.articleId, FetchMode.NONE)?.id
                }
            }
        }

        Given("유효한 아티클이 있고") {
            val articleId = ArticleId(IdUtils.generate())
            fakeRepo.save(
                Article.init(
                    id = articleId,
                    writerId = WriterId(1),
                ),
            )
            And(" 스테이징 command가 있고") {
                val title = ArticleTitle("title")
                val content = ArticleContent("content")
                val command =
                    ArticleStagingCommandUseCase.ArticleStagingCommand(
                        articleId = articleId,
                        title = title,
                        content = content,
                        thumbnailUrl = null,
                        categoryId = null,
                    )
                When("아티클 스테이징 usecase를 실행하면") {
                    val info = sut.staging(command)

                    Then("아티클 스테이징이 실행된다") {
                        val foundOne = fakeRepo.findById(info.articleId, FetchMode.NONE)
                        foundOne shouldNotBe null
                        info.articleId shouldBe foundOne!!.id
                        foundOne.stagingSnapShot shouldNotBe null
                        title shouldBe foundOne.stagingSnapShot!!.title
                        content shouldBe foundOne.stagingSnapShot!!.content
                    }
                }
            }

            And("커밋 command가 있고") {
                val title = ArticleTitle("title")
                val content = ArticleContent("content")
                val command =
                    ArticleCommitCommandUseCase.ArticleCommitCommand(
                        articleId = articleId,
                        title = title,
                        content = content,
                        thumbnailUrl = null,
                        categoryId = null,
                    )
                When("아티클 커밋 usecase를 실행하면") {
                    val info = sut.commit(command)

                    Then("아티클 커밋이 저장된다") {
                        val foundOne = fakeRepo.findById(info.articleId, FetchMode.NONE)
                        foundOne shouldNotBe null
                        info.articleId shouldBe foundOne!!.id
                        foundOne.stagingSnapShot shouldBe null
                        title shouldBe foundOne.title
                        content shouldBe foundOne.content
                    }
                }
            }
        }
    }

    private fun fakeArticleRepository() =
        object : ArticleRepository {
            private val map = mutableMapOf<ArticleId, Article>()

            override suspend fun nextId(): ArticleId {
                return ArticleId(IdUtils.generate())
            }

            override suspend fun findById(
                id: ArticleId,
                mode: FetchMode,
            ): Article? {
                return map[id]
            }

            override suspend fun findAll(mode: FetchMode): List<Article> {
                return map.values.toList()
            }

            override suspend fun deleteById(id: ArticleId) {
                map.remove(id)
            }

            override suspend fun save(entity: Article): Article {
                map[entity.id] = entity
                return entity
            }
        }
}
