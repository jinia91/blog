package kr.co.jiniaslog.memo.domain.memo

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class MemoOrderingTests : CustomBehaviorSpec() {
    init {
        Given("메모가 생성될 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)

            When("기본 sequence로 생성하면") {
                val memo = Memo.init(authorId, folderId)
                Then("sequence가 설정된다") {
                    memo.sequence shouldBe Memo.DEFAULT_SEQUENCE
                }
            }

            When("특정 sequence로 생성하면") {
                val memo = Memo.init(authorId, folderId, sequence = "0|i00007:")
                Then("해당 sequence가 설정된다") {
                    memo.sequence shouldBe "0|i00007:"
                }
            }
        }

        Given("메모의 sequence를 변경할 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo = Memo.init(authorId, folderId, sequence = "0|i00007:")

            When("새로운 sequence로 변경하면") {
                memo.changeSequence("0|i00003:")
                Then("sequence가 변경된다") {
                    memo.sequence shouldBe "0|i00003:"
                }
            }
        }

        Given("두 메모 사이에 삽입할 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo1 = Memo.init(authorId, folderId, sequence = "0|i00007:")
            val memo2 = Memo.init(authorId, folderId, sequence = "0|i0000f:")

            When("중간 sequence로 새 메모를 생성하면") {
                val middleSequence = "0|i0000b:"
                val newMemo = Memo.init(authorId, folderId, sequence = middleSequence)
                Then("올바른 순서가 된다") {
                    newMemo.sequence shouldBe "0|i0000b:"
                    (memo1.sequence < newMemo.sequence) shouldBe true
                    (newMemo.sequence < memo2.sequence) shouldBe true
                }
            }
        }

        Given("여러 메모가 순차적으로 존재할 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo1 = Memo.init(authorId, folderId, sequence = "0|i00007:")
            val memo2 = Memo.init(authorId, folderId, sequence = "0|i0000f:")
            val memo3 = Memo.init(authorId, folderId, sequence = "0|i0000r:")

            When("맨 앞으로 이동시키면") {
                memo3.changeSequence("0|i00003:")
                Then("첫 번째 메모보다 앞에 위치한다") {
                    memo3.sequence shouldBe "0|i00003:"
                    (memo3.sequence < memo1.sequence) shouldBe true
                }
            }

            When("맨 뒤로 이동시키면") {
                memo1.changeSequence("0|i0000z:")
                Then("마지막 메모보다 뒤에 위치한다") {
                    memo1.sequence shouldBe "0|i0000z:"
                    (memo1.sequence > memo3.sequence) shouldBe true
                }
            }
        }

        Given("동일한 폴더 안에 메모들이 있을 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo1 = Memo.init(authorId, folderId, sequence = "0|i00007:")
            val memo2 = Memo.init(authorId, folderId, sequence = "0|i0000f:")
            val memo3 = Memo.init(authorId, folderId, sequence = "0|i0000r:")

            When("중간 메모를 다른 위치로 이동시키면") {
                memo2.changeSequence("0|i0000n:")
                Then("순서가 올바르게 유지된다") {
                    memo2.sequence shouldBe "0|i0000n:"
                    (memo1.sequence < memo2.sequence) shouldBe true
                    (memo2.sequence < memo3.sequence) shouldBe true
                }
            }
        }

        Given("메모가 하나만 존재할 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo = Memo.init(authorId, folderId, sequence = "0|i00007:")

            When("sequence를 변경하면") {
                memo.changeSequence("0|i0000f:")
                Then("변경이 정상적으로 적용된다") {
                    memo.sequence shouldBe "0|i0000f:"
                }
            }
        }

        Given("매우 가까운 sequence를 가진 메모들이 있을 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo1 = Memo.init(authorId, folderId, sequence = "0|i00007:")
            val memo2 = Memo.init(authorId, folderId, sequence = "0|i00008:")

            When("그 사이에 메모를 삽입하면") {
                val middleSequence = "0|i00007i:"
                val newMemo = Memo.init(authorId, folderId, sequence = middleSequence)
                Then("정확한 중간값이 계산된다") {
                    newMemo.sequence shouldBe "0|i00007i:"
                    (memo1.sequence < newMemo.sequence) shouldBe true
                    (newMemo.sequence < memo2.sequence) shouldBe true
                }
            }
        }

        Given("최소 sequence를 가진 메모가 있을 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo = Memo.init(authorId, folderId, sequence = "0|000000:")

            When("메모가 생성되면") {
                Then("최소 sequence가 허용된다") {
                    memo.sequence shouldBe "0|000000:"
                }
            }

            When("더 큰 값으로 변경하면") {
                memo.changeSequence("0|i00007:")
                Then("변경이 정상적으로 적용된다") {
                    memo.sequence shouldBe "0|i00007:"
                }
            }
        }

        Given("큰 sequence 값을 가진 메모들이 있을 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo1 = Memo.init(authorId, folderId, sequence = "0|zzzzzz:")
            val memo2 = Memo.init(authorId, folderId, sequence = "1|000000:")

            When("그 사이에 메모를 삽입하면") {
                val middleSequence = "0|zzzzzzi:"
                val newMemo = Memo.init(authorId, folderId, sequence = middleSequence)
                Then("순서가 올바르게 유지된다") {
                    (memo1.sequence < newMemo.sequence) shouldBe true
                    (newMemo.sequence < memo2.sequence) shouldBe true
                }
            }
        }

        Given("기본 sequence를 가진 메모가 있을 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo = Memo.init(authorId, folderId, sequence = Memo.DEFAULT_SEQUENCE)

            When("메모가 생성되면") {
                Then("기본 sequence가 허용된다") {
                    memo.sequence shouldBe Memo.DEFAULT_SEQUENCE
                }
            }
        }

        Given("같은 sequence를 가진 메모들이 있을 때") {
            val authorId = AuthorId(1)
            val folderId = FolderId(1)
            val memo1 = Memo.init(authorId, folderId, sequence = "0|i00007:")
            val memo2 = Memo.init(authorId, folderId, sequence = "0|i00007:")

            When("메모들이 생성되면") {
                Then("같은 sequence가 허용된다") {
                    memo1.sequence shouldBe memo2.sequence
                }
            }

            When("하나의 sequence를 변경하면") {
                memo1.changeSequence("0|i0000f:")
                Then("독립적으로 변경된다") {
                    memo1.sequence shouldBe "0|i0000f:"
                    memo2.sequence shouldBe "0|i00007:"
                }
            }
        }

        Given("null 폴더를 가진 메모가 있을 때") {
            val authorId = AuthorId(1)
            val memo = Memo.init(authorId, null, sequence = "0|i00007:")

            When("메모가 생성되면") {
                Then("null 폴더가 허용된다") {
                    memo.parentFolderId shouldBe null
                    memo.sequence shouldBe "0|i00007:"
                }
            }
        }

        Given("서로 다른 폴더에 속한 메모들이 있을 때") {
            val authorId = AuthorId(1)
            val folder1 = FolderId(1)
            val folder2 = FolderId(2)
            val memo1 = Memo.init(authorId, folder1, sequence = "0|i00007:")
            val memo2 = Memo.init(authorId, folder2, sequence = "0|i00007:")

            When("메모들이 생성되면") {
                Then("다른 폴더에서 같은 sequence를 가질 수 있다") {
                    memo1.sequence shouldBe memo2.sequence
                    memo1.parentFolderId shouldNotBe memo2.parentFolderId
                }
            }
        }

        Given("메모가 폴더를 변경할 때") {
            val authorId = AuthorId(1)
            val folder1 = FolderId(1)
            val folder2 = FolderId(2)
            val memo = Memo.init(authorId, folder1, sequence = "0|i00007:")

            When("다른 폴더로 이동하면") {
                memo.setParentFolder(folder2)
                Then("폴더가 변경되고 sequence는 유지된다") {
                    memo.parentFolderId shouldBe folder2
                    memo.sequence shouldBe "0|i00007:"
                }
            }

            When("null 폴더로 이동하면") {
                memo.setParentFolder(null)
                Then("폴더가 null이 되고 sequence는 유지된다") {
                    memo.parentFolderId shouldBe null
                    memo.sequence shouldBe "0|i00007:"
                }
            }
        }
    }
}
