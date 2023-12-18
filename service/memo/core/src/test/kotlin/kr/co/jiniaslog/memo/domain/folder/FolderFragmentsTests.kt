package kr.co.jiniaslog.memo.domain.folder

import io.kotest.assertions.throwables.shouldThrow
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class FolderFragmentsTests : CustomBehaviorSpec() {
    init {
        given("FolderId 생성시") {
            `when`("생성자에 음수를 넣으면") {
                then("실패한다") {
                    shouldThrow<IllegalArgumentException> {
                        FolderId(-1)
                    }
                }
            }
        }

        given("FolderName 생성시") {
            `when`("생성자에 빈 문자열을 넣으면") {
                then("실패한다") {
                    shouldThrow<IllegalArgumentException> {
                        FolderName("")
                    }
                }
            }
        }
    }
}
