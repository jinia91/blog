package kr.co.jiniaslog.memo.domain.folder

import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class FolderOrderingTests : CustomBehaviorSpec() {
    init {
        Given("폴더가 생성될 때") {
            val authorId = AuthorId(1)
            When("기본 sequence로 생성하면") {
                val folder = Folder.init(authorId)
                Then("sequence가 설정된다") {
                    folder.sequence shouldBe Folder.DEFAULT_SEQUENCE
                }
            }
            When("특정 sequence로 생성하면") {
                val folder = Folder.init(authorId, sequence = "0|i00007:")
                Then("해당 sequence가 설정된다") {
                    folder.sequence shouldBe "0|i00007:"
                }
            }
        }

        Given("폴더의 sequence를 변경할 때") {
            val authorId = AuthorId(1)
            val folder = Folder.init(authorId, sequence = "0|i00007:")
            When("새로운 sequence로 변경하면") {
                folder.changeSequence("0|i00003:")
                Then("sequence가 변경된다") {
                    folder.sequence shouldBe "0|i00003:"
                }
            }
        }

        Given("두 폴더 사이에 삽입할 때") {
            val authorId = AuthorId(1)
            val folder1 = Folder.init(authorId, sequence = "0|i00007:")
            val folder2 = Folder.init(authorId, sequence = "0|i0000f:")
            When("중간 sequence로 새 폴더를 생성하면") {
                val middleSequence = "0|i0000b:"
                val newFolder = Folder.init(authorId, sequence = middleSequence)
                Then("올바른 순서가 된다") {
                    newFolder.sequence shouldBe "0|i0000b:"
                    (folder1.sequence < newFolder.sequence) shouldBe true
                    (newFolder.sequence < folder2.sequence) shouldBe true
                }
            }
        }

        Given("여러 폴더가 순차적으로 존재할 때") {
            val authorId = AuthorId(1)
            val folder1 = Folder.init(authorId, sequence = "0|i00007:")
            val folder2 = Folder.init(authorId, sequence = "0|i0000f:")
            val folder3 = Folder.init(authorId, sequence = "0|i0000r:")

            When("맨 앞으로 이동시키면") {
                folder3.changeSequence("0|i00003:")
                Then("첫 번째 폴더보다 앞에 위치한다") {
                    folder3.sequence shouldBe "0|i00003:"
                    (folder3.sequence < folder1.sequence) shouldBe true
                }
            }

            When("맨 뒤로 이동시키면") {
                folder1.changeSequence("0|i0000z:")
                Then("마지막 폴더보다 뒤에 위치한다") {
                    folder1.sequence shouldBe "0|i0000z:"
                    (folder1.sequence > folder3.sequence) shouldBe true
                }
            }
        }

        Given("동일한 부모를 가진 폴더들이 있을 때") {
            val authorId = AuthorId(1)
            val parentFolder = Folder.init(authorId)
            val child1 = Folder.init(authorId, parentFolder.entityId, sequence = "0|i00007:")
            val child2 = Folder.init(authorId, parentFolder.entityId, sequence = "0|i0000f:")
            val child3 = Folder.init(authorId, parentFolder.entityId, sequence = "0|i0000r:")

            When("중간 폴더를 다른 위치로 이동시키면") {
                child2.changeSequence("0|i0000n:")
                Then("순서가 올바르게 유지된다") {
                    child2.sequence shouldBe "0|i0000n:"
                    (child1.sequence < child2.sequence) shouldBe true
                    (child2.sequence < child3.sequence) shouldBe true
                }
            }
        }

        Given("폴더가 하나만 존재할 때") {
            val authorId = AuthorId(1)
            val folder = Folder.init(authorId, sequence = "0|i00007:")

            When("sequence를 변경하면") {
                folder.changeSequence("0|i0000f:")
                Then("변경이 정상적으로 적용된다") {
                    folder.sequence shouldBe "0|i0000f:"
                }
            }
        }

        Given("매우 가까운 sequence를 가진 폴더들이 있을 때") {
            val authorId = AuthorId(1)
            val folder1 = Folder.init(authorId, sequence = "0|i00007:")
            val folder2 = Folder.init(authorId, sequence = "0|i00008:")

            When("그 사이에 폴더를 삽입하면") {
                val middleSequence = "0|i00007i:"
                val newFolder = Folder.init(authorId, sequence = middleSequence)
                Then("정확한 중간값이 계산된다") {
                    newFolder.sequence shouldBe "0|i00007i:"
                    (folder1.sequence < newFolder.sequence) shouldBe true
                    (newFolder.sequence < folder2.sequence) shouldBe true
                }
            }
        }

        Given("최소 sequence를 가진 폴더가 있을 때") {
            val authorId = AuthorId(1)
            val folder = Folder.init(authorId, sequence = "0|000000:")

            When("폴더가 생성되면") {
                Then("최소 sequence가 허용된다") {
                    folder.sequence shouldBe "0|000000:"
                }
            }

            When("더 큰 값으로 변경하면") {
                folder.changeSequence("0|i00007:")
                Then("변경이 정상적으로 적용된다") {
                    folder.sequence shouldBe "0|i00007:"
                }
            }
        }

        Given("큰 sequence 값을 가진 폴더들이 있을 때") {
            val authorId = AuthorId(1)
            val folder1 = Folder.init(authorId, sequence = "0|zzzzzz:")
            val folder2 = Folder.init(authorId, sequence = "1|000000:")

            When("그 사이에 폴더를 삽입하면") {
                val middleSequence = "0|zzzzzzi:"
                val newFolder = Folder.init(authorId, sequence = middleSequence)
                Then("순서가 올바르게 유지된다") {
                    (folder1.sequence < newFolder.sequence) shouldBe true
                    (newFolder.sequence < folder2.sequence) shouldBe true
                }
            }
        }

        Given("기본 sequence를 가진 폴더가 있을 때") {
            val authorId = AuthorId(1)
            val folder = Folder.init(authorId, sequence = Folder.DEFAULT_SEQUENCE)

            When("폴더가 생성되면") {
                Then("기본 sequence가 허용된다") {
                    folder.sequence shouldBe Folder.DEFAULT_SEQUENCE
                }
            }
        }

        Given("같은 sequence를 가진 폴더들이 있을 때") {
            val authorId = AuthorId(1)
            val folder1 = Folder.init(authorId, sequence = "0|i00007:")
            val folder2 = Folder.init(authorId, sequence = "0|i00007:")

            When("폴더들이 생성되면") {
                Then("같은 sequence가 허용된다") {
                    folder1.sequence shouldBe folder2.sequence
                }
            }

            When("하나의 sequence를 변경하면") {
                folder1.changeSequence("0|i0000f:")
                Then("독립적으로 변경된다") {
                    folder1.sequence shouldBe "0|i0000f:"
                    folder2.sequence shouldBe "0|i00007:"
                }
            }
        }
    }
}
