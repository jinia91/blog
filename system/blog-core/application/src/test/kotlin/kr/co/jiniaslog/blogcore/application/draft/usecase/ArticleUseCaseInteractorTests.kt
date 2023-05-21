package kr.co.jiniaslog.blogcore.application.draft.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleUseCaseInteractor
import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.blogcore.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import utils.TestUtils
import java.time.LocalDateTime

class ArticleUseCaseInteractorTests : BehaviorSpec() {
    private val articleIdGenerator: ArticleIdGenerator = mockk()
    private val articleRepository: ArticleRepository = mockk(relaxed = true)
    private val transactionHandler: TransactionHandler = mockk()
    private val userServiceClient: UserServiceClient = mockk()
    private val sut: ArticleUseCaseInteractor = ArticleUseCaseInteractor(
        articleIdGenerator = articleIdGenerator,
        articleRepository = articleRepository,
        transactionHandler = transactionHandler,
        userServiceClient = userServiceClient,
    )

    init {
        TestUtils.doTransactionDefaultStubbing(transactionHandler)

        Given("공개 아티클 명령이 주어질때") {
            val command = ArticleCommands.PostArticleCommand(
                writerId = UserId(value = 6343),
                title = "title",
                content = "content",
                thumbnailUrl = "thumbnailUrl",
                categoryId = CategoryId(value = 1),
                tags = setOf(TagId(1), TagId(2)),
                draftArticleId = null,
            )
            And("validation을 통과하지 못하면 - writerId가 존재하지 않는다면") {
                every { userServiceClient.userExists(command.writerId) } returns false
                When("공개 아티클 명령을 실행하면") {
                    Then("예외가 발생한다") {
                        shouldThrow<ResourceNotFoundException> {
                            sut.post(command)
                        }
                    }
                }
            }

            And("validations를 통과한다면") {
                every { userServiceClient.userExists(command.writerId) } returns true
                every { articleIdGenerator.generate() } returns ArticleId(value = 1)
                When("공개 아티클 명령을 실행하면") {
                    val result = sut.post(command)
                    Then("아티클이 등록된다") {
                        verify(exactly = 1) { articleRepository.save(any()) }
                        result.articleId shouldBe ArticleId(value = 1)
                    }
                }
            }
        }

        Given("공개 아티클 수정 명령이 주어지면") {
            val articleId = ArticleId(value = 2)
            val command = ArticleCommands.EditArticleCommand(
                articleId = articleId,
                title = "title",
                content = "content",
                thumbnailUrl = "thumbnailUrl",
                categoryId = CategoryId(value = 1),
                tags = setOf(TagId(1), TagId(2)),
                writerId = UserId(value = 1),
            )
            And("validation을 통과하지 못하면 - writerId가 존재하지 않는다면") {
                every { userServiceClient.userExists(command.writerId) } returns false
                When("공개 아티클 수정 명령을 실행하면") {
                    Then("예외가 발생한다") {
                        shouldThrow<ResourceNotFoundException> {
                            sut.edit(command)
                        }
                    }
                }
            }

            And("validation을 통과하지 못하면 - articleId가 존재하지 않는다면") {
                every { articleRepository.findById(command.articleId) } returns null
                When("공개 아티클 수정 명령을 실행하면") {
                    Then("예외가 발생한다") {
                        shouldThrow<ResourceNotFoundException> {
                            sut.edit(command)
                        }
                    }
                }
            }
            And("validation을 통과한다면") {
                val mockArticle = Article.Factory.from(
                    id = articleId,
                    title = "empty",
                    content = "empty",
                    thumbnailUrl = "thumbnailUrl",
                    categoryId = CategoryId(value = 1),
                    tags = setOf(TagId(1), TagId(2)),
                    writerId = UserId(value = 1),
                    hit = 0,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                )

                every { userServiceClient.userExists(command.writerId) } returns true
                every { articleRepository.findById(command.articleId) } returns mockArticle
                When("공개 아티클 수정 명령을 실행하면") {
                    val result = sut.edit(command)
                    Then("아티클이 수정된다") {
                        verify(exactly = 1) { articleRepository.save(mockArticle) }
                        result.articleId shouldBe ArticleId(value = 2)
                    }
                }
            }
        }

        Given("공개아티클을 조회할때") {
            val articleId = ArticleId(value = 2)
            And("해당 아티클이 존재하지 않는다면") {
                every { articleRepository.findById(articleId) } returns null
                When("공개아티클을 조회하면") {
                    val result = sut.getArticle(articleId)
                    Then("null이 반환된다") {
                        result shouldBe null
                    }
                }
            }

            And("해당 아티클이 존재한다면") {
                val mockArticle = Article.Factory.from(
                    id = articleId,
                    title = "empty",
                    content = "empty",
                    thumbnailUrl = "thumbnailUrl",
                    categoryId = CategoryId(value = 1),
                    tags = setOf(TagId(1), TagId(2)),
                    writerId = UserId(value = 1),
                    hit = 0,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                )
                every { articleRepository.findById(articleId) } returns mockArticle
                When("공개아티클을 조회하면") {
                    val result = sut.getArticle(articleId)
                    Then("해당 아티클이 조회된다") {
                        result shouldNotBe null
                        result!!.id shouldBe ArticleId(value = 2)
                    }
                }
            }
        }
    }
}
