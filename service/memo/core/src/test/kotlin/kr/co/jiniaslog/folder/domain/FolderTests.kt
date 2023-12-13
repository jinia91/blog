package kr.co.jiniaslog.folder.domain

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class FolderTests : CustomBehaviorSpec() {
    init {
        Given("유효한 폴더 생성 조건이 주어지면") {
            val authorId = AuthorId(1)
            When("폴더를 생성하면") {
                val newFolder = Folder.init(authorId)
                Then("폴더가 생성된다.") {
                    newFolder shouldNotBe null
                    newFolder.id shouldNotBe null
                    newFolder.parent shouldBe null
                    newFolder.name shouldBe FolderName.UNTITLED
                }
            }
            And("특정 폴더 하위이면") {
                val parentFolder = Folder.init(authorId)
                When("폴더를 생성하면") {
                    val newFolder = Folder.init(authorId, parentFolder.id)
                    Then("폴더가 생성된다.") {
                        newFolder shouldNotBe null
                        newFolder.id shouldNotBe null
                        newFolder.name shouldBe FolderName.UNTITLED
                        newFolder.parent shouldBe parentFolder.id
                    }
                }
            }
        }
    }
}
