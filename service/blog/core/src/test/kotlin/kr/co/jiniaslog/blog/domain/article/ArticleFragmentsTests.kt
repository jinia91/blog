package kr.co.jiniaslog.blog.domain.article

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.blog.CustomBehaviorSpec
import org.assertj.core.api.Assertions.assertThat

class ArticleFragmentsTests : CustomBehaviorSpec() {
    init {
        Given("ArticleId 생성시") {
            And("ArticleId의 값이 양수이면") {
                val value = 1L
                When("생성시") {
                    Then("생성된다") {
                        ArticleId(value)
                    }
                }
            }
            And("ArticleId의 값이 0이하이면") {
                val value = 0L
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            ArticleId(value)
                        }
                    }
                }
            }
        }

        Given("ArticleCommitVersion 생성시") {
            And("ArticleCommitVersion의 값이 양수이면") {
                val value = 1L
                When("생성시") {
                    Then("생성된다") {
                        ArticleCommitVersion(value)
                    }
                }
            }
            And("ArticleCommitVersion의 값이 0이하이면") {
                val value = 0L
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            ArticleCommitVersion(value)
                        }
                    }
                }
            }
        }

        Given("ArticleTitle 생성시") {
            And("ArticleTitle의 값이 1자 이상이면") {
                val value = "title"
                When("생성시") {
                    Then("생성된다") {
                        ArticleTitle(value)
                    }
                }
            }
            And("ArticleTitle의 값이 1자 미만이면") {
                val value = ""
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            ArticleTitle(value)
                        }
                    }
                }
            }
        }

        Given("ArticleContent 생성시") {
            And("ArticleContent의 값이 0자 이상이면") {
                val value = "content"
                When("생성시") {
                    Then("생성된다") {
                        ArticleContent(value)
                    }
                }
            }
        }

        Given("articleContent가 둘 있을때") {
            val sut = ArticleContent("content")
            val sut2 = ArticleContent("content2")
            When("두 컨텐츠의 변경값을 구하면") {
                val diff = sut.calculateDelta(sut2)
                Then("델타 타입이 반환된다") {
                    assertThat(diff).isInstanceOf(ArticleContentDelta::class.java)
                }
            }
        }

        Given("articleContent가 있고") {
            val sut = ArticleContent("content")
            And("변경값이 있을때") {
                val delta = sut.calculateDelta(ArticleContent("content2"))
                When("변경값을 적용하면") {
                    val result = sut.apply(delta)
                    Then("변경된 값이 반환된다") {
                        result.value shouldBe "content2"
                    }
                }
            }
        }

        Given("article thumbnail 생성시") {
            And("thumbnail url이 있으면") {
                val url = "url"
                When("생성시") {
                    Then("생성된다") {
                        ArticleThumbnailUrl(url)
                    }
                }
            }
            And("thumbnail url이 없으면") {
                val url = ""
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            ArticleThumbnailUrl(url)
                        }
                    }
                }
            }
        }

        Given("snapshotId 생성시") {
            And("snapshotId의 값이 양수이면") {
                val value = 1L
                When("생성시") {
                    Then("생성된다") {
                        StagingSnapShotId(value)
                    }
                }
            }
            And("snapshotId의 값이 0이하이면") {
                val value = 0L
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            StagingSnapShotId(value)
                        }
                    }
                }
            }
        }
    }
}
