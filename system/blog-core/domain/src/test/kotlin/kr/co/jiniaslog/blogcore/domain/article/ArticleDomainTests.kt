package kr.co.jiniaslog.blogcore.domain.article

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId

internal class ArticleDomainTests : BehaviorSpec() {
    init {
        Given("draft Article이 주어졌을 때") {
            val mockArticle = Article.Factory.newDraftOne(
                id = ArticleId(value = 7907),
                userId = UserId(value = 8191),
                title = "aliquid",
                content = "omittam",
                thumbnailUrl = null,
                categoryId = null,
                tags = setOf(),
            )

            When("Article이 publish되려면") {
                Then("썸네일과 카테고리 아이디가 있어야한다") {
                    shouldThrow<ArticleNotValidException> {
                        mockArticle.publish()
                    }
                }
            }

            When("Article이 publish되려면") {
                Then("썸네일과 카테고리 아이디가 있어야한다") {
                    shouldThrow<ArticleNotValidException> {
                        mockArticle.publish()
                    }
                }
            }

            And("썸네일이 있더라도 카테고리아이디가 없으면") {
                mockArticle.edit(
                    title = "quam",
                    content = "definitiones",
                    thumbnailUrl = "http://www.bing.com/search?q=sanctus",
                    categoryId = null,
                    tags = setOf(),
                )
                When("Article.publish()가") {
                    Then("실패한다") {
                        shouldThrow<ArticleNotValidException> {
                            mockArticle.publish()
                        }
                    }
                }
            }

            And("카테고리아이디가 있더라도 썸네일이 없으면") {
                mockArticle.edit(
                    title = "quam",
                    content = "definitiones",
                    thumbnailUrl = null,
                    categoryId = CategoryId(value = 1),
                    tags = setOf(),
                )
                When("Article이 publish가") {
                    Then("실패한다") {
                        shouldThrow<ArticleNotValidException> {
                            mockArticle.publish()
                        }
                    }
                }
            }

            And("카테고리아이디 썸네일이 모두 있더라도 태그가 없으면") {
                mockArticle.edit(
                    title = "quam",
                    content = "definitiones",
                    thumbnailUrl = "null",
                    categoryId = CategoryId(value = 1),
                    tags = setOf(),
                )
                When("Article이 publish가") {
                    Then("실패한다") {
                        shouldThrow<ArticleNotValidException> {
                            mockArticle.publish()
                        }
                    }
                }
            }

            And("카테고리아이디 썸네일 태그가 모두 있으면") {
                mockArticle.edit(
                    title = "quam",
                    content = "definitiones",
                    thumbnailUrl = "null",
                    categoryId = CategoryId(value = 1),
                    tags = setOf(TagId(1)),
                )
                When("Article publish가") {
                    mockArticle.publish()
                    Then("성공한다") {
                        mockArticle.status shouldBe Article.ArticleStatus.PUBLISHED
                    }
                }
            }
        }

        Given("태그 아이디 없이 publish 아티클을 만들려하면") {
            Then("실패한다") {
                shouldThrow<ArticleNotValidException> {
                    Article.Factory.newPublishedArticle(
                        id = ArticleId(value = 6340),
                        userId = UserId(value = 2077),
                        title = "laudem",
                        content = "offendit",
                        thumbnailUrl = "http://www.bing.com/search?q=eloquentiam",
                        categoryId = CategoryId(value = 1482),
                        tags = setOf(),
                    )
                }
            }
        }

        Given("Published 아티클이 주어졌을때") {
            val publishedArticle = Article.Factory.newPublishedArticle(
                id = ArticleId(value = 6340),
                userId = UserId(value = 2077),
                title = "laudem",
                content = "offendit",
                thumbnailUrl = "http://www.bing.com/search?q=eloquentiam",
                categoryId = CategoryId(value = 1482),
                tags = setOf(TagId(1)),
            )

            When("Article.edit()시 썸네일과 카테고리아이디 태그가 없다면") {
                Then("실패한다") {
                    shouldThrow<ArticleNotValidException> {
                        publishedArticle.edit(
                            title = "quam",
                            content = "definitiones",
                            thumbnailUrl = null,
                            categoryId = null,
                            tags = setOf(),
                        )
                    }
                }
            }

            When("Article.edit()시 썸네일과 카테고리아이디 태그가 없다면") {
                Then("실패한다") {
                    shouldThrow<ArticleNotValidException> {
                        publishedArticle.edit(
                            title = "quam",
                            content = "definitiones",
                            thumbnailUrl = null,
                            categoryId = null,
                            tags = setOf(),
                        )
                    }
                }
            }

            When("Article.edit()시 썸네일과 카테고리아이디 없다면") {
                Then("실패한다") {
                    shouldThrow<ArticleNotValidException> {
                        publishedArticle.edit(
                            title = "quam",
                            content = "definitiones",
                            thumbnailUrl = null,
                            categoryId = null,
                            tags = setOf(TagId(1)),
                        )
                    }
                }
            }

            When("Article.drafting()") {
                publishedArticle.drafting()
                Then("Article의 상태가 Draft로 변경된다") {
                    publishedArticle.status shouldBe Article.ArticleStatus.DRAFT
                }
            }
        }

        Given("draft Article이 주어지고") {
            val mockArticle = Article.Factory.newDraftOne(
                id = ArticleId(value = 707),
                userId = UserId(value = 891),
                title = "aliqid",
                content = "oittam",
                thumbnailUrl = null,
                categoryId = null,
                tags = setOf(),
            )
            When("article.hit()") {
                mockArticle.hit()
                Then("article의 hit가 증가하지 않는다") {
                    mockArticle.hit shouldBe 0
                }
            }
        }

        Given("published Article이 주어지고") {
            val mockArticle = Article.Factory.newPublishedArticle(
                id = ArticleId(value = 707),
                userId = UserId(value = 891),
                title = "aliqid",
                content = "oittam",
                thumbnailUrl = "test",
                categoryId = CategoryId(value = 1),
                tags = setOf(TagId(2)),
            )
            When("article.hit()") {
                mockArticle.hit()
                Then("article의 hit가 증가한다") {
                    mockArticle.hit shouldBe 1
                    mockArticle.createdDate shouldBe null
                    mockArticle.updatedDate shouldBe null
                }
            }
        }
    }
}
