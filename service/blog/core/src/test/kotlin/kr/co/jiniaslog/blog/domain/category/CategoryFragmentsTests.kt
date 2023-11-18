package kr.co.jiniaslog.blog.domain.category

import io.kotest.assertions.throwables.shouldThrow
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
    }
}
