package kr.co.jiniaslog.blogcore.domain.article

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import org.junit.platform.commons.annotation.Testable

@Testable
internal class TempArticleServiceImplTest : BehaviorSpec() {
    private val tempArticleRepository: TempArticleRepository = mockk(relaxed = true)
    private val sut: TempArticleServiceImpl = TempArticleServiceImpl(tempArticleRepository)

    init {
        Given("임시 아티클이 주어지고") {
            val tempArticle = TempArticle.Factory.newTempOne(
                userId = UserId(1L),
                title = "title",
                content = "content",
                thumbnailUrl = "thumbnailUrl",
                categoryId = CategoryId(1L),
                tags = setOf(TagId(1L), TagId(2L)),
            )

            When("sut.saveTempArticle(tempArticle)") {
                sut.saveTempArticle(tempArticle)

                Then("임시 아티클이 저장된다.") {
                    verify(exactly = 1) { tempArticleRepository.save(tempArticle) }
                }
            }

            And("주어진 임시 아티클이 저장되어 있다면") {
                every { tempArticleRepository.findTemp(tempArticle.id) } returns tempArticle

                When("sut.findTempArticle()") {
                    val result = sut.findTempArticle()

                    Then("임시 아티클이 반환된다.") {
                        result shouldBe tempArticle
                    }
                }
            }

            When("sut.deleteTempArticle()") {
                sut.deleteTempArticle()

                Then("임시 아티클이 삭제된다.") {
                    verify(exactly = 1) { tempArticleRepository.delete() }
                }
            }
        }
    }
}
