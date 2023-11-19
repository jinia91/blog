package kr.co.jiniaslog.blog.domain.writer

import kr.co.jiniaslog.shared.CustomBehaviorSpec

class WriterTests : CustomBehaviorSpec() {
    init {
        Given("Writer 생성시") {
            And("WriterId, WriterName이 주어지면") {
                val id = WriterId(1L)
                val name = WriterName("name")
                When("생성시") {
                    Then("생성된다") {
                        Writer.create(
                            id = id,
                            name = name,
                        )
                    }
                }
            }
        }
    }
}
