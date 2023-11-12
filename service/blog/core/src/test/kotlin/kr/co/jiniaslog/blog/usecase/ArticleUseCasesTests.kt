package kr.co.jiniaslog.blog.usecase

import io.kotest.core.spec.style.BehaviorSpec
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.WriterId
import org.junit.jupiter.api.Assertions.assertEquals

class ArticleUseCasesTests : BehaviorSpec() {
    private val articleUseCases = ArticleUseCasesImpl()

    init {
        Given("글쓰기를 시작하면") {
            val articleInitCommand = ArticleInitCommand(writerId = WriterId(1))
            When("최초의 아티클을 생성하고 최초 커밋한다.") {
                val initialInfo = articleUseCases.init(articleInitCommand)
                Then("최초의 아티클이 생성된다.") {
                    assertEquals(initialInfo.articleId, ArticleId(1))
                }
            }
        }
    }
}
