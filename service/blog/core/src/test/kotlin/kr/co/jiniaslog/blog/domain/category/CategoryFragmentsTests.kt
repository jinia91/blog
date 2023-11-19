package kr.co.jiniaslog.blog.domain.category

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class CategoryFragmentsTests : CustomBehaviorSpec() {
    init {
        Given("categoryId 생성시") {
            And("categoryId의 값이 양수이면") {
                val value = 1L
                When("생성시") {
                    Then("생성된다") {
                        CategoryId(value)
                    }
                }
            }
            And("categoryId의 값이 0이하이면") {
                val value = 0L
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            CategoryId(value)
                        }
                    }
                }
            }
        }

        Given("categoryName 생성시") {
            And("categoryName의 값이 1자 이상이면") {
                val value = "name"
                When("생성시") {
                    val categoryName = CategoryName(value)
                    Then("생성된다") {
                        categoryName.value shouldBe value
                    }
                }
            }
            And("categoryName의 값이 0자 이하이면") {
                val value = ""
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            CategoryName(value)
                        }
                    }
                }
            }
            And("categoryName의 값이 20자 이상이면") {
                val value = "123456789012345678901"
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            CategoryName(value)
                        }
                    }
                }
            }
        }

        Given("sortingOrder 생성시") {
            And("sortingOrder의 값이 양수이면") {
                val value = 1
                When("생성시") {
                    Then("생성된다") {
                        SortingOrder(value)
                    }
                }
            }
            And("sortingOrder의 값이 0이하이면") {
                val value = 0
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            SortingOrder(value)
                        }
                    }
                }
            }
            And("무작위로 여러개 생성되고") {
                val list =
                    (1..10).map {
                        val randomNumber = (1..100).random()
                        SortingOrder(randomNumber)
                    }
                When("정렬을 하면") {
                    val sorted = list.sorted()
                    Then("정렬된다") {
                        sorted.forEachIndexed { index, sortingOrder ->
                            if (index == sorted.lastIndex) return@forEachIndexed
                            sortingOrder shouldBeLessThanOrEqualTo sorted[index + 1]
                        }
                    }
                }
            }
        }
    }
}
