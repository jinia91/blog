package kr.co.jiniaslog.memo.usecase

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.domain.FolderTestFixtures
import kr.co.jiniaslog.memo.domain.MemoTestFixtures
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.outbound.FolderRepository
import kr.co.jiniaslog.memo.outbound.MemoRepository
import kr.co.jiniaslog.memo.queries.FolderQueriesFacade
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchy
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
            parent = dummyParentFolder.id
        )
        folderRepository.save(emptyChildFolder)

        withClue("분류된 메모") {
            (1..10).map {
                memoRepository.save(
                    MemoTestFixtures.build(
                        parentFolderId = dummyParentFolder.id
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
        val result = sut.handle(IGetFoldersAllInHierirchy.Query(null))

        // then
        result.folderInfos.size shouldBe 2
        result.folderInfos.find {
            it.id == dummyParentFolder.id
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

    @Test
    fun `키워드로 조회시 가상 폴더 하에 메모만 조회된다`() {
        // given
        val dummyParentFolder = FolderTestFixtures.build()
        folderRepository.save(dummyParentFolder)

        val emptyChildFolder = FolderTestFixtures.build(
            parent = dummyParentFolder.id
        )
        folderRepository.save(emptyChildFolder)

        withClue("키워드가 있는 메모") {
            (1..10).map {
                memoRepository.save(
                    MemoTestFixtures.build(title = MemoTitle("검색"))
                )
            }
        }

        withClue("키워드가 없는 메모") {
            memoRepository.save(
                MemoTestFixtures.build()
            )
        }

        // when
        val result = sut.handle(IGetFoldersAllInHierirchy.Query("검색"))

        // then
        result.folderInfos.size shouldBe 1
        result.folderInfos.find {
            it.id == null
        }?.let { unclassifiedFolderInfo ->
            unclassifiedFolderInfo.children.size shouldBe 0
            unclassifiedFolderInfo.memos.size shouldBe 10
        }
    }
}