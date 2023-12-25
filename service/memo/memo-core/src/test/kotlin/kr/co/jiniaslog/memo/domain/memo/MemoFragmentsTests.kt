package kr.co.jiniaslog.memo.domain.memo

import io.kotest.assertions.throwables.shouldThrow
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class MemoFragmentsTests : CustomBehaviorSpec() {
    init {
        given("MemoId") {
            `when`("생성자에 음수를 넣으면") {
                then("실패한다") {
                    shouldThrow<IllegalArgumentException> {
                        MemoId(-1)
                    }
                }
            }
        }

        given("AuthorId") {
            `when`("생성자에 음수를 넣으면") {
                then("실패한다") {
                    shouldThrow<IllegalArgumentException> {
                        AuthorId(-1)
                    }
                }
            }
        }
    }
}
