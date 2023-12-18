package kr.co.jiniaslog.memo.domain.memo

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.shared.CustomBehaviorSpec

internal class MemoTests : CustomBehaviorSpec() {
    init {
        // context: 새로운 메모 작성시
        Given("새로운 메모 작성시 작성자 id만 주어지고") {
            val authorId = AuthorId(1)
            When("메모 초기화를 하면") {
                val sutMemo = Memo.init(authorId = authorId)
                Then("메모가 초기화된다") {
                    sutMemo.id shouldNotBe null
                    sutMemo.id.value shouldNotBe 0
                    sutMemo.authorId shouldBe authorId
                    sutMemo.state shouldBe MemoState.DRAFT
                }
                Then("persist 되지 않은상태여야한다") {
                    sutMemo.isPersisted shouldBe false
                }
            }
        }
        Given("새로운 메모 작성시 작성자 id와 제목이 주어지고") {
            val authorId = AuthorId(1)
            val title = MemoTitle("title")
            When("메모 초기화를 하면") {
                val sutMemo = Memo.init(title = title, authorId = authorId)
                Then("메모가 초기화된다") {
                    sutMemo.id shouldNotBe null
                    sutMemo.id.value shouldNotBe 0
                    sutMemo.authorId shouldBe authorId
                    sutMemo.state shouldBe MemoState.DRAFT
                    sutMemo.title shouldBe title
                }
                Then("persist 되지 않은상태여야한다") {
                    sutMemo.isPersisted shouldBe false
                }
            }
        }

        // context: 메모 업데이트
        Given("유효한 메모가 주어지고") {
            val sutMemo = Memo.init(authorId = AuthorId(1))
            And("업데이트할 제목이 주어지고") {
                val title = MemoTitle("title")
                When("메모 업데이트를 하면") {
                    sutMemo.update(
                        title = title,
                    )
                    Then("메모의 제목이 변경된다") {
                        sutMemo.title shouldBe title
                    }
                }
            }
            And("업데이트할 내용이 주어지고") {
                val content = MemoContent("content")
                When("메모 업데이트를 하면") {
                    sutMemo.update(
                        content = content,
                    )
                    Then("메모의 내용이 변경된다") {
                        sutMemo.content shouldBe content
                    }
                }
            }
        }

        // context: 메모 상태 변경 -> committed
        Given("메모가 주어지고") {
            val sutMemo = Memo.init(authorId = AuthorId(1))
            When("커밋을 하면") {
                sutMemo.commit(
                    title = MemoTitle("title"),
                    content = MemoContent("content"),
                )
                Then("커밋상태가 된다") {
                    sutMemo.state shouldBe MemoState.COMMITTED
                }
            }
        }
    }
}
