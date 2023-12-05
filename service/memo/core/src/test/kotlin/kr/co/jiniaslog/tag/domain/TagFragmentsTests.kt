package kr.co.jiniaslog.tag.domain

import io.kotest.assertions.throwables.shouldThrow
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class TagFragmentsTests : CustomBehaviorSpec() {
    init {
        Given("유효하지 않은 태그 아이디가 주어지고") {
            When("태그 아이디를 생성하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<IllegalArgumentException> {
                        TagId(-1)
                    }
                }
            }
        }
        Given("유효하지 않은 태그명이 주어지고") {
            When("태그명을 생성하면") {
                Then("예외가 발생한다.") {
                    shouldThrow<IllegalArgumentException> {
                        TagName("")
                    }
                }
            }
        }
    }
}
