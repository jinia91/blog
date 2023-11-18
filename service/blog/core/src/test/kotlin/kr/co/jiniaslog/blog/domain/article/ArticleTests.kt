package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.CustomBehaviorSpec
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime

class ArticleTests : CustomBehaviorSpec() {
    init {
        Given("유효한 아티클이 있고") {

            val sut =
                Article.from(
                    id = ArticleId(1),
                    writerId = WriterId(1),
                    articleHistory = mutableListOf(ArticleCommit.initCommit()),
                    stagingSnapShot = null,
                    head = ArticleCommitVersion(1),
                    checkout = ArticleCommitVersion(1),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                )

            And("유효한 커밋 데이터가 있고") {

                val title = ArticleTitle("title")
                val content = ArticleContent("content")
                val thumbnailUrl = ArticleThumbnailUrl("thumbnailUrl")
                val categoryId = CategoryId(1)

                When("커밋을 하면") {

                    sut.commit(
                        title = title,
                        newContent = content,
                        thumbnailUrl = thumbnailUrl,
                        categoryId = categoryId,
                    )

                    Then("정상적인 커밋이 된다") {
                        assertThat(sut.history.size).isEqualTo(2)
                        assertThat(sut.head).isEqualTo(sut.latestCommit.id)
                        assertThat(sut.checkout).isEqualTo(sut.latestCommit.id)
                        assertThat(sut.stagingSnapShot).isNull()
                    }

                    Then("커밋 이벤트가 등록된다") {
                        val events = sut.getEvents()
                        assertThat(events).isNotEmpty
                        assertThat(events.size).isEqualTo(1)
                        assertThat(events[0]).isInstanceOf(ArticleCommitted::class.java)
                    }
                }
            }
        }
    }
}
