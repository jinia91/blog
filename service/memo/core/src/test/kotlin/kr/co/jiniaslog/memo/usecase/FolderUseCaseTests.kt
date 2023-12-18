package kr.co.jiniaslog.memo.usecase

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.memo.domain.folder.Folder
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.fakes.FakeFolderRepository
import kr.co.jiniaslog.memo.usecase.impl.FolderUseCases
import kr.co.jiniaslog.memo.usecase.impl.FolderUseCasesFacade
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class FolderUseCaseTests : CustomBehaviorSpec() {
    private var folderRepository: FolderRepository = FakeFolderRepository()
    private val sut: FolderUseCasesFacade =
        FolderUseCases(
            folderRepository = folderRepository,
        )

    init {
        /**
         * I Create New Folder
         */
        Given("유효한 폴더 초기화 요청이 있고") {
            val command =
                ICreateNewFolder.Command(
                    authorId = AuthorId(1),
                )
            When("폴더 초기화를 하면") {
                val info = sut.handle(command)
                Then("폴더가 초기화된다.") {
                    info.id shouldNotBe null
                    folderRepository.findById(info.id) shouldNotBe null
                }
            }
        }

        /**
         * I Change Folder Name
         */
        Given("유효한 폴더 이름 변경 요청이 있고") {
            val folder =
                folderRepository.save(
                    Folder.init(
                        authorId = AuthorId(1),
                    ),
                )
            val command =
                IChangeFolderName.Command(
                    folderId = folder.id,
                    name = FolderName("name"),
                )
            When("폴더 이름 변경을 하면") {
                val info = sut.handle(command)
                Then("폴더 이름이 변경된다.") {
                    info.folderId shouldNotBe null
                    folderRepository.findById(info.folderId) shouldNotBe null
                }
            }
        }

        /**
         * I Delete Folder Recursively - 실제 재귀적 삭제는 db에서 처리하므로 테스트는 하위 폴더가 없는 경우만
         */
        Given("유효한 폴더 삭제 요청이 있고") {
            val folder =
                folderRepository.save(
                    Folder.init(
                        authorId = AuthorId(1),
                    ),
                )
            val command =
                IDeleteFoldersRecursively.Command(
                    folderId = folder.id,
                )
            When("폴더 삭제를 하면") {
                val info = sut.handle(command)
                Then("폴더가 삭제된다.") {
                    info.folderId shouldNotBe null
                    folderRepository.findById(info.folderId) shouldBe null
                }
            }
        }
        /**
         * I Make Relationship Folder And Folder
         */
        Given("유효한 폴더 관계 설정 요청이 있고") {
            val parentFolder =
                folderRepository.save(
                    Folder.init(
                        authorId = AuthorId(1),
                    ),
                )
            val childFolder =
                folderRepository.save(
                    Folder.init(
                        authorId = AuthorId(1),
                    ),
                )
            val command =
                IMakeRelationShipFolderAndFolder.Command(
                    parentFolderId = parentFolder.id,
                    childFolderId = childFolder.id,
                )
            When("폴더 관계 설정을 하면") {
                val info = sut.handle(command)
                Then("폴더 관계가 설정된다.") {
                    info.parentFolderId shouldNotBe null
                    info.childFolderId shouldNotBe null
                    folderRepository.findById(info.childFolderId) shouldNotBe null
                }
            }
        }
    }
}
