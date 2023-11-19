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
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.writer.WriterId
import kr.co.jiniaslog.blog.fakeArticleRepository
import kr.co.jiniaslog.blog.usecase.ArticleCommitCommandUseCase.ArticleCommitCommand
import kr.co.jiniaslog.shared.CustomBehaviorSpec
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
                    info.articleId shouldBe fakeRepo.findById(info.articleId)?.id
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

            And("아티클 삭제 command가 있고") {
                val command =
                    ArticleDeleteCommandUseCase.ArticleDeleteCommand(
                        articleId = articleId,
                    )
                When("아티클 삭제 usecase를 실행하면") {
                    val info = sut.delete(command)

                    Then("아티클 삭제가 실행된다") {
                        val foundOne = fakeRepo.findById(command.articleId)
                        foundOne shouldBe null
                    }
                }
            }

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
                        val foundOne = fakeRepo.findById(info.articleId)
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
                    ArticleCommitCommand(
                        articleId = articleId,
                        title = title,
                        content = content,
                        thumbnailUrl = null,
                        categoryId = null,
                    )
                When("아티클 커밋 usecase를 실행하면") {
                    val info = sut.commit(command)

                    Then("아티클 커밋이 저장된다") {
                        val foundOne = fakeRepo.findById(info.articleId)
                        foundOne shouldNotBe null
                        info.articleId shouldBe foundOne!!.id
                        foundOne.stagingSnapShot shouldBe null
                        title shouldBe foundOne.title
                        content shouldBe foundOne.content
                    }
                    And("공개하기 유효한 아티클 commit command가 있고") {
                        val command2 =
                            ArticleCommitCommand(
                                articleId = articleId,
                                title = title,
                                content = content,
                                thumbnailUrl = ArticleThumbnailUrl("thumbnailUrl"),
                                categoryId = CategoryId(1),
                            )
                        sut.commit(command2)
                        And("아티클 공개 command 가 있고") {
                            val command3 =
                                ArticlePublishCommandUseCase.ArticlePublishCommand(
                                    articleId = articleId,
                                    headVersion = info.commitId,
                                )
                            When("아티클 공개 usecase를 실행하면") {
                                val info2 = sut.publish(command3)

                                Then("아티클 공개가 실행된다") {
                                    val foundOne = fakeRepo.findById(info2.articleId)
                                    foundOne shouldNotBe null
                                    info.articleId shouldBe foundOne!!.id
                                    foundOne.stagingSnapShot shouldBe null
                                    foundOne.status shouldBe ArticleStatus.PUBLISHED
                                }
                            }
                        }
                    }
                }
            }
        }

        Given("아티클이 없고") {
            val articleId = ArticleId(IdUtils.generate())
            And("아티클 커밋 command가 있고") {
                val title = ArticleTitle("title")
                val content = ArticleContent("content")
                val command =
                    ArticleCommitCommand(
                        articleId = articleId,
                        title = title,
                        content = content,
                        thumbnailUrl = null,
                        categoryId = null,
                    )
                When("아티클 커밋 usecase를 실행하면") {
                    Then("아티클 커밋이 실패한다") {
                        shouldThrow<IllegalArgumentException> {
                            sut.commit(command)
                        }
                    }
                }
            }

            And("아티클 삭제 command가 있고") {
                val command =
                    ArticleDeleteCommandUseCase.ArticleDeleteCommand(
                        articleId = articleId,
                    )
                When("아티클 삭제 usecase를 실행하면") {
                    Then("아티클 삭제가 실패한다") {
                        shouldThrow<IllegalArgumentException> {
                            sut.delete(command)
                        }
                    }
                }
            }

            And("아티클 staging command가 있고") {
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
                    Then("아티클 스테이징이 실패한다") {
                        shouldThrow<IllegalArgumentException> {
                            sut.staging(command)
                        }
                    }
                }
            }
        }
    }
}
