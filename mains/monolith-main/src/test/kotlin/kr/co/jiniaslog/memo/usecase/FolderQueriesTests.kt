package kr.co.jiniaslog.memo.usecase

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.domain.FolderTestFixtures
import kr.co.jiniaslog.memo.domain.MemoTestFixtures
import kr.co.jiniaslog.memo.domain.folder.FolderRepository
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.queries.FolderQueriesFacade
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchyByAuthorId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class FolderQueriesTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: FolderQueriesFacade

    @Autowired
    lateinit var folderRepository: FolderRepository

    @Autowired
    lateinit var memoRepository: MemoRepository

    @Test
    fun `키워드 없이 모든 폴더 조회시, 메모와 함께 미분류 메모를 담는 가상 폴더를 포함하여 계층화되어 조회된다`() {
        // given
        val dummyParentFolder = FolderTestFixtures.build()
        folderRepository.save(dummyParentFolder)

        val emptyChildFolder = FolderTestFixtures.build(
            parent = dummyParentFolder.entityId
        )
        folderRepository.save(emptyChildFolder)

        withClue("분류된 메모") {
            (1..10).map {
                memoRepository.save(
                    MemoTestFixtures.build(
                        parentFolderId = dummyParentFolder.entityId,
                    )
                )
            }
        }

        withClue("미분류 메모") {
            memoRepository.save(
                MemoTestFixtures.build(
                    parentFolderId = null
                )
            )
        }

        // when
        val result = sut.handle(IGetFoldersAllInHierirchyByAuthorId.Query(MemoTestFixtures.defaultAuthorId))

        // then
        result.folderInfos.size shouldBe 2
        result.folderInfos.find {
            it.id == dummyParentFolder.entityId.value
        }?.let { parentFolderInfo ->
            parentFolderInfo.children.size shouldBe 1
            parentFolderInfo.memos.size shouldBe 10
        }

        result.folderInfos.find {
            it.id == null
        }?.let { unclassifiedFolderInfo ->
            unclassifiedFolderInfo.children.size shouldBe 0
            unclassifiedFolderInfo.memos.size shouldBe 1
        }
    }
}
