package kr.co.jiniaslog.blog.domain.article

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.writer.WriterId
import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import kr.co.jiniaslog.message.nexus.event.ArticleDeleted
import kr.co.jiniaslog.message.nexus.event.ArticlePublished
import kr.co.jiniaslog.shared.CustomBehaviorSpec
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime
import kotlin.random.Random

class ArticleTests : CustomBehaviorSpec() {
    init {
        Given("유효한 아티클 아이디와 작성자 아이디가 있고") {
            val articleId = ArticleId(Random.nextLong(1, 100))
            val writerId = WriterId(Random.nextLong(1, 100))
            When("아티클을 생성하면") {
                Article.init(
                    id = articleId,
                    writerId = writerId,
                )
                Then("정상적으로 생성된다") {
                    val sut =
                        Article.init(
                            id = articleId,
                            writerId = writerId,
                        )
                    assertThat(sut.id).isEqualTo(articleId)
                    assertThat(sut.writerId).isEqualTo(writerId)
                    assertThat(sut.history.size).isEqualTo(1)
                    assertThat(sut.stagingSnapShot).isNull()
                    assertThat(sut.head).isEqualTo(sut.latestCommit.id)
                }
            }
        }

        Given("유효한 기본 아티클이 있고") {

            val sut =
                Article.from(
                    id = ArticleId(1),
                    writerId = WriterId(1),
                    articleHistory = mutableListOf(ArticleCommit.initCommit()),
                    stagingSnapShot = null,
                    status = ArticleStatus.DRAFT,
                    head = ArticleCommitVersion(1),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                )

            When("아티클을 삭제하면") {
                sut.delete()
                Then("삭제 이벤트가 등록된다") {
                    assertThat(sut.getEvents().first()).isInstanceOf(ArticleDeleted::class.java)
                }
            }

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
                        assertThat(sut.stagingSnapShot).isNull()
                        sut.title shouldBe title
                        sut.content shouldBe content
                        sut.thumbnailUrl shouldBe thumbnailUrl
                        sut.categoryId shouldBe categoryId
                        sut.writerId shouldBe WriterId(1)
                    }

                    Then("head와 checkout은 갱신된다") {
                        assertThat(sut.head).isEqualTo(sut.latestCommit.id)
                    }

                    Then("커밋 이벤트가 등록된다") {
                        val events = sut.getEvents()
                        assertThat(events).isNotEmpty
                        assertThat(events.size).isEqualTo(1)
                        assertThat(events[0]).isInstanceOf(ArticleCommitted::class.java)
                    }
                }
            }

            And("데이터가 비어있고") {
                val title = null
                val content = ArticleContent.EMPTY
                val thumbnailUrl = null
                val categoryId = null

                When("커밋을 하면") {
                    sut.commit(
                        title = title,
                        newContent = content,
                        thumbnailUrl = thumbnailUrl,
                        categoryId = categoryId,
                    )

                    Then("정상적인 커밋이 된다") {
                        assertThat(sut.history.size).isEqualTo(2)
                        assertThat(sut.stagingSnapShot).isNull()
                    }

                    Then("head와 checkout은 갱신된다") {
                        assertThat(sut.head).isEqualTo(sut.latestCommit.id)
                    }

                    Then("커밋 이벤트가 등록된다") {
                        val events = sut.getEvents()
                        assertThat(events).isNotEmpty
                        assertThat(events.size).isEqualTo(1)
                        assertThat(events[0]).isInstanceOf(ArticleCommitted::class.java)
                    }
                }
            }

            And("여러 커밋들이 있고") {
                val title = ArticleTitle("title")
                val content = ArticleContent("content")
                val thumbnailUrl = ArticleThumbnailUrl("thumbnailUrl")
                val categoryId = CategoryId(1)

                for (i in 1 until 10) {
                    sut.commit(
                        title = title,
                        newContent = content + ArticleContent(i.toString()),
                        thumbnailUrl = thumbnailUrl,
                        categoryId = categoryId,
                    )
                }

                When("특정 커밋을 재현하면") {
                    val index = Random.nextLong(1, 10)
                    val replayedContent = sut.getContentByCommitVer(sut.history[index.toInt()].id)

                    Then("컨텐츠가 재현된다") {
                        assertThat(sut.history.size).isEqualTo(10)
                        assertThat(replayedContent).isNotNull
                        assertThat(replayedContent.value).isEqualTo("content$index")
                    }
                }
            }

            And("스냅샷을 만들 데이터가 있고") {
                val title = ArticleTitle("title")
                val content = ArticleContent("content")
                val thumbnailUrl = ArticleThumbnailUrl("thumbnailUrl")
                val categoryId = CategoryId(5)

                When("스냅샷을 저장하면") {
                    sut.staging(
                        title = title,
                        content = content,
                        thumbnailUrl = thumbnailUrl,
                        categoryId = categoryId,
                    )

                    Then("스냅샷이 만들어진다") {
                        assertThat(sut.stagingSnapShot).isNotNull
                        assertThat(sut.stagingSnapShot?.title).isEqualTo(title)
                        assertThat(sut.stagingSnapShot?.content).isEqualTo(content)
                        assertThat(sut.stagingSnapShot?.thumbnailUrl).isEqualTo(thumbnailUrl)
                        assertThat(sut.stagingSnapShot?.categoryId).isEqualTo(categoryId)
                    }
                }
            }
        }

        Given("아티클을 공개해야하고") {

            And("아티클의 제목이 없고") {
                val sut =
                    Article.from(
                        id = ArticleId(1),
                        writerId = WriterId(1),
                        articleHistory = mutableListOf(ArticleCommit.initCommit()),
                        stagingSnapShot = null,
                        status = ArticleStatus.DRAFT,
                        head = ArticleCommitVersion(1),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                When("아티클 공개를 하면") {
                    Then("예외가 발생한다") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                sut.publish(sut.latestCommit.id)
                            }
                    }
                }
            }

            And("아티클의 내용이 없고") {
                val sut =
                    Article.from(
                        id = ArticleId(1),
                        writerId = WriterId(1),
                        articleHistory = mutableListOf(ArticleCommit.initCommit()),
                        stagingSnapShot = null,
                        status = ArticleStatus.DRAFT,
                        head = ArticleCommitVersion(1),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                When("아티클 공개를 하면") {
                    Then("예외가 발생한다") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                sut.publish(sut.latestCommit.id)
                            }
                    }
                }
            }

            And("아티클의 썸네일이 없고") {
                val sut =
                    Article.from(
                        id = ArticleId(1),
                        writerId = WriterId(1),
                        articleHistory = mutableListOf(ArticleCommit.initCommit()),
                        stagingSnapShot = null,
                        status = ArticleStatus.DRAFT,
                        head = ArticleCommitVersion(1),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                When("아티클 공개를 하면") {
                    Then("예외가 발생한다") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                sut.publish(sut.latestCommit.id)
                            }
                    }
                }
            }

            And("아티클의 카테고리가 없고") {
                val sut =
                    Article.from(
                        id = ArticleId(1),
                        writerId = WriterId(1),
                        articleHistory = mutableListOf(ArticleCommit.initCommit()),
                        stagingSnapShot = null,
                        status = ArticleStatus.DRAFT,
                        head = ArticleCommitVersion(1),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                When("아티클 공개를 하면") {
                    Then("예외가 발생한다") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                sut.publish(sut.latestCommit.id)
                            }
                    }
                }
            }

            And("공개하기 유효한 아티클이고") {
                val sut =
                    Article.from(
                        id = ArticleId(1),
                        writerId = WriterId(1),
                        articleHistory = mutableListOf(ArticleCommit.initCommit()),
                        stagingSnapShot = null,
                        status = ArticleStatus.DRAFT,
                        head = ArticleCommitVersion(1),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                sut.commit(
                    title = ArticleTitle("title"),
                    newContent = ArticleContent("content"),
                    thumbnailUrl = ArticleThumbnailUrl("thumbnailUrl"),
                    categoryId = CategoryId(1),
                )

                sut.getEvents() // consume init event

                When("아티클 공개를 하면") {
                    sut.publish(sut.latestCommit.id)

                    Then("아티클이 공개된다") {
                        assertThat(sut.status).isEqualTo(ArticleStatus.PUBLISHED)
                    }

                    Then("공개 이벤트가 등록된다") {
                        val events = sut.getEvents()
                        assertThat(events).isNotEmpty
                        assertThat(events.size).isEqualTo(1)
                        assertThat(events[0]).isInstanceOf(ArticlePublished::class.java)
                    }
                }
            }
        }
    }
}