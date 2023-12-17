package kr.co.jiniaslog.memo.usecase

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.fakes.FakeFolderRepository
import kr.co.jiniaslog.fakes.FakeMemoRepository
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.usecase.impl.MemoUseCases
import kr.co.jiniaslog.memo.usecase.impl.MemoUseCasesFacade
import kr.co.jiniaslog.shared.CustomBehaviorSpec

internal class MemoUseCaseTests : CustomBehaviorSpec() {
    private var memoRepository: MemoRepository = FakeMemoRepository()
    private var folderRepository: FolderRepository = FakeFolderRepository()
    private val sut: MemoUseCasesFacade =
        MemoUseCases(
            memoRepository = memoRepository,
            folderRepository = folderRepository,
        )

    init {
        /**
         * 메모 초기화
         */
        Given("유효한 메모 초기화 요청이 있고") {
            val command =
                IInitMemo.Command(
                    content = MemoContent("content"),
                    authorId = AuthorId(1),
                    title = MemoTitle("title"),
                )
            When("메모 초기화를 하면") {
                val info = sut.handle(command)
                Then("메모가 초기화된다.") {
                    info.id shouldNotBe null
                    memoRepository.findById(info.id) shouldNotBe null
                }
            }
        }
        Given("메모 초기화 요청이 제목만 주어지고") {
            val command =
                IInitMemo.Command(
                    authorId = AuthorId(1),
                    title = MemoTitle("title"),
                )
            When("메모 초기화를 하면") {
                val info = sut.handle(command)
                Then("메모가 초기화된다.") {
                    info.id shouldNotBe null
                    memoRepository.findById(info.id) shouldNotBe null
                }
            }
        }

        /**
         * 메모 업데이트
         */
        Given("유효한 메모가 주어지고") {
            val memo =
                Memo.init(
                    authorId = AuthorId(1),
                )
            memoRepository.save(memo)

            And("업데이트할 제목이 주어지고") {
                val command =
                    IUpdateMemo.Command.UpdateForm(
                        memoId = memo.id,
                        title = MemoTitle("title"),
                    )
                When("메모 업데이트를 하면") {
                    sut.handle(command)
                    Then("메모의 제목이 변경된다") {
                        val foundMemo = memoRepository.findById(memo.id)!!
                        foundMemo.title shouldBe MemoTitle("title")
                        foundMemo.content shouldBe MemoContent("")
                    }
                }
            }
            And("업데이트할 내용이 주어지고") {
                val command =
                    IUpdateMemo.Command.UpdateForm(
                        memoId = memo.id,
                        content = MemoContent("content"),
                    )
                When("메모 업데이트를 하면") {
                    sut.handle(command)
                    Then("메모의 내용이 변경된다") {
                        val foundMemo = memoRepository.findById(memo.id)!!
                        foundMemo.title shouldBe MemoTitle("")
                        foundMemo.content shouldBe MemoContent("content")
                    }
                }
            }
            And("참조 추가 커맨드가 주어지고") {
                val reference =
                    Memo.init(
                        authorId = AuthorId(1),
                    )
                memoRepository.save(reference)
                val command =
                    IUpdateMemo.Command.AddReference(
                        memoId = memo.id,
                        referenceId = reference.id,
                    )
                When("메모 업데이트를 하면") {
                    sut.handle(command)
                    Then("메모의 참조가 추가된다") {
                        val foundMemo = memoRepository.findById(memo.id)!!
                        foundMemo.references.size shouldBe 1
                        foundMemo.references.first().referenceId shouldBe reference.id
                    }
                }
            }
            And("참조 삭제 커맨드가 주어지고") {
                val reference =
                    Memo.init(
                        authorId = AuthorId(1),
                    )
                memoRepository.save(reference)
                memo.addReference(reference.id)
                val command =
                    IUpdateMemo.Command.RemoveReference(
                        memoId = memo.id,
                        referenceId = reference.id,
                    )
                When("메모 업데이트를 하면") {
                    sut.handle(command)
                    Then("메모의 참조가 삭제된다") {
                        val foundMemo = memoRepository.findById(memo.id)!!
                        foundMemo.references.size shouldBe 0
                    }
                }
            }
        }
    }
}