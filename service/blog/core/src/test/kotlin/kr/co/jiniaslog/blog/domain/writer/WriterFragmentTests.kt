package kr.co.jiniaslog.blog.domain.writer

import io.kotest.assertions.throwables.shouldThrow
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class WriterFragmentTests : CustomBehaviorSpec() {
    init {
        Given("WriterId 생성시") {
            And("WriterId의 값이 양수이면") {
                val value = 1L
                When("생성시") {
                    Then("생성된다") {
                        WriterId(value)
                    }
                }
            }
            And("WriterId의 값이 0이하이면") {
                val value = 0L
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            WriterId(value)
                        }
                    }
                }
            }
        }

        Given("WriterName 생성시") {
            And("WriterName의 값이 1자 이상이면") {
                val value = "name"
                When("생성시") {
                    Then("생성된다") {
                        WriterName(value)
                    }
                }
            }
            And("WriterName의 값이 공백이면") {
                val value = ""
                When("생성시") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            WriterName(value)
                        }
                    }
                }
            }
        }
    }
}
