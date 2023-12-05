package kr.co.jiniaslog.tag.domain

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.memo.domain.tag.Tag
import kr.co.jiniaslog.memo.domain.tag.TagName
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class TagTests : CustomBehaviorSpec() {
    init {
        Given("유효한 태그명이 주어지고") {
            val tagName = TagName("tagName")
            When("태그를 생성하면") {
                val tag = Tag.init(tagName)
                Then("태그가 생성된다.") {
                    tag.id shouldNotBe null
                    tag.name shouldBe tagName
                    tag.isPersisted shouldNotBe true
                }
            }
        }
    }
}
