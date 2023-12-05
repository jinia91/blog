package kr.co.jiniaslog.memo.domain

import io.kotest.assertions.throwables.shouldThrow
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTagId
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

        given("MemoTagId") {
            `when`("생성자에 음수를 넣으면") {
                then("실패한다") {
                    shouldThrow<IllegalArgumentException> {
                        MemoTagId(-1)
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
