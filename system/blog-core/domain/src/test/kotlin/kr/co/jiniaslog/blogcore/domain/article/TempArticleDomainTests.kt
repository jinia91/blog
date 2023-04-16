package kr.co.jiniaslog.blogcore.domain.article

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.user.UserId

internal class TempArticleDomainTests : BehaviorSpec() {
    init {
        Given("temp Article은") {
            val mockArticle = TempArticle.Factory.from(
                writerId = UserId(value = 8191),
            )

            val mockArticle2 = TempArticle.Factory.from(
                writerId = UserId(value = 4151),
                title = "aliquid",
                content = "null",
                thumbnailUrl = "null",
                categoryId = CategoryId(2),
            )

            When("생성될때") {
                Then("항상 id는 1이다") {
                    mockArticle.id.value shouldBe 1L
                    mockArticle2.id.value shouldBe 1L
                }
            }
        }
    }
}
