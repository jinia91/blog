package kr.co.jiniaslog.blogcore.application.draft.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.CreateDraftArticleCommand
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.DeleteDraftArticleCommand
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.UpdateDraftArticleCommand
import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.blogcore.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import utils.TestUtils
import java.time.LocalDateTime

class DraftArticleUseCaseInteractorTest : BehaviorSpec() {
    private val draftArticleIdGenerator: DraftArticleIdGenerator = mockk()
    private val draftArticleRepository: DraftArticleRepository = mockk(relaxed = true)
    private val transactionHandler: TransactionHandler = mockk()
    private val userServiceClient: UserServiceClient = mockk()
    private val sut: DraftArticleUseCaseInteractor = DraftArticleUseCaseInteractor(
        draftArticleIdGenerator = draftArticleIdGenerator,
        draftArticleRepository = draftArticleRepository,
        transactionHandler = transactionHandler,
        userServiceClient = userServiceClient,
    )

    init {
        TestUtils.doTransactionDefaultStubbing(transactionHandler)

        Given("아티클 초안 등록 명령이 주어질때") {
            val command = CreateDraftArticleCommand(
                writerId = UserId(value = 6343),
                title = null,
                content = null,
                thumbnailUrl = null,
            )

            and("validation을 통과하지 못하면") {
                every { userServiceClient.userExists(command.writerId) } returns false
                When("아티클 초안 등록 명령을 실행하면") {
                    Then("예외가 발생한다") {
                        shouldThrow<ResourceNotFoundException> {
                            sut.create(command)
                        }
                    }
                }
            }

            and("저자 아이디가 존재한다면") {
                every { userServiceClient.userExists(command.writerId) } returns true
                every { draftArticleIdGenerator.generate() } returns DraftArticleId(value = 1)
                When("임시 아티클 등록 명령을 실행하면") {
                    val result = sut.create(command)
                    Then("정상 저장 된다.") {
                        verify(exactly = 1) { draftArticleRepository.save(any()) }
                        result.draftArticleId shouldBe DraftArticleId(value = 1)
                    }
                }
            }
        }

        Given("아티클 초안 수정 명령이 주어질때") {
            val draftArticleId = DraftArticleId(value = 5912)
            val command = UpdateDraftArticleCommand(
                writerId = UserId(value = 4687),
                draftArticleId = draftArticleId,
                title = "title",
                content = "content",
                thumbnailUrl = "thumbnailUrl",
            )

            and("validation을 통과하지 못하면") {
                every { userServiceClient.userExists(command.writerId) } returns false
                When("아티클 초안 등록 명령을 실행하면") {
                    Then("예외가 발생한다") {
                        shouldThrow<ResourceNotFoundException> {
                            sut.update(command)
                        }
                    }
                }
            }

            And("validation을 통과하고") {
                every { userServiceClient.userExists(command.writerId) } returns true
                And("해당 아티클이 없다면") {
                    every { draftArticleRepository.findById(draftArticleId) } returns null
                    When("아티클 초안 수정 명령을 실행하면") {
                        Then("예외가 발생한다.") {
                            shouldThrow<ResourceNotFoundException> {
                                sut.update(command)
                            }
                        }
                    }
                }

                And("해당 아티클이 존재한다면") {
                    val mockArticle = DraftArticle.Factory.from(
                        id = draftArticleId,
                        writerId = UserId(value = 4687),
                        title = null,
                        content = null,
                        thumbnailUrl = null,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )
                    every { draftArticleRepository.findById(draftArticleId) } returns mockArticle

                    When("아티클 초안 수정 명령을 실행하면") {
                        sut.update(command)
                        Then("정상적으로 수정된다.") {
                            verify(exactly = 1) { draftArticleRepository.save(mockArticle) }
                        }
                    }
                }
            }
        }

        Given("아티클 초안 삭제 명령이 주어질때") {
            val draftArticleId = DraftArticleId(value = 1912)
            val command = DeleteDraftArticleCommand(draftArticleId = draftArticleId)

            and("아티클 초안이 없다면") {
                every { draftArticleRepository.findById(command.draftArticleId) } returns null
                When("아티클 초안 삭제 명령을 실행하면") {
                    Then("예외가 발생한다") {
                        shouldThrow<ResourceNotFoundException> {
                            sut.delete(command)
                        }
                    }
                }
            }

            And("해당 아티클이 존재한다면") {
                val mockArticle = DraftArticle.Factory.from(
                    id = draftArticleId,
                    writerId = UserId(value = 4687),
                    title = null,
                    content = null,
                    thumbnailUrl = null,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                )
                every { draftArticleRepository.findById(draftArticleId) } returns mockArticle

                When("아티클 초안 삭제 명령을 실행하면") {
                    sut.delete(command)
                    Then("정상적으로 삭제된다.") {
                        verify(exactly = 1) { draftArticleRepository.deleteById(mockArticle.id) }
                    }
                }
            }
        }
        Given("아티클 초안 조회 쿼리가 존재할때") {
            val query = DraftArticleId(5L)
            And("아티클이 없다면") {
                every { draftArticleRepository.findById(query) } returns null
                When("아티클 초안 조회 쿼리를 실행하면") {
                    Then("예외가 발생한다.") {
                        shouldThrow<ResourceNotFoundException> { sut.getDraftArticle(query) }
                    }
                }
            }
            And("해당 아티클이 존재한다면") {
                val mockArticle = DraftArticle.Factory.from(
                    id = query,
                    writerId = UserId(value = 4687),
                    title = null,
                    content = null,
                    thumbnailUrl = null,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                )
                every { draftArticleRepository.findById(query) } returns mockArticle
                When("아티클 초안 조회 쿼리를 실행하면") {
                    val result = sut.getDraftArticle(query)
                    Then("정상적으로 조회된다.") {
                        result shouldBe mockArticle
                    }
                }
            }
        }
    }
}
