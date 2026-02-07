package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.ai.outbound.MemoCommandService
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MemoToolsTests : SimpleUnitTestContext() {

    private lateinit var memoCommandService: MemoCommandService
    private lateinit var memoTools: MemoTools

    @BeforeEach
    fun setup() {
        memoCommandService = mockk<MemoCommandService>()
        memoTools = MemoTools(memoCommandService)
        memoTools.setAuthorId(1L)
    }

    @Nested
    inner class GetCurrentDateTimeTests {
        @Test
        fun `현재 날짜와 시간 정보를 반환한다`() {
            // when
            val result = memoTools.getCurrentDateTime()

            // then
            result shouldContain "현재 시간:"
            result shouldContain "오늘:"
            result shouldContain "내일:"
            result shouldContain "모레:"
            result shouldContain "이번주 토요일:"
            result shouldContain "이번주 일요일:"
            result shouldContain "다음주 월요일:"
        }

        @Test
        fun `날짜 형식이 올바르게 포맷된다`() {
            // when
            val result = memoTools.getCurrentDateTime()
            val now = LocalDateTime.now()
            val expectedYear = now.format(DateTimeFormatter.ofPattern("yyyy년"))

            // then
            result shouldContain expectedYear
            result shouldContain "월"
            result shouldContain "일"
        }
    }

    @Nested
    inner class CreateMemoTests {
        @Test
        fun `메모 생성에 성공하면 MEMO_CREATED 포맷으로 반환한다`() {
            // given
            val title = "테스트 메모"
            val content = "메모 내용"
            val memoId = 123L

            every { memoCommandService.createMemo(1L, title, content) } returns memoId

            // when
            val result = memoTools.createMemo(title, content)

            // then
            result shouldBe "MEMO_CREATED:$memoId:$title:메모가 생성되었습니다."
            verify(exactly = 1) { memoCommandService.createMemo(1L, title, content) }
        }

        @Test
        fun `긴 제목과 내용으로 메모를 생성할 수 있다`() {
            // given
            val title = "아주 긴 제목입니다".repeat(5)
            val content = "아주 긴 내용입니다.\n".repeat(100)
            val memoId = 456L

            every { memoCommandService.createMemo(1L, title, content) } returns memoId

            // when
            val result = memoTools.createMemo(title, content)

            // then
            result shouldStartWith "MEMO_CREATED:$memoId:"
            verify(exactly = 1) { memoCommandService.createMemo(1L, title, content) }
        }
    }

    @Nested
    inner class UpdateMemoTests {
        @Test
        fun `메모 수정에 성공하면 MEMO_UPDATED 메시지를 반환한다`() {
            // given
            val memoId = 123L
            val newTitle = "수정된 제목"
            val newContent = "수정된 내용"

            every { memoCommandService.updateMemo(memoId, 1L, newTitle, newContent) } returns memoId

            // when
            val result = memoTools.updateMemo(memoId, newTitle, newContent)

            // then
            result shouldBe "MEMO_UPDATED:$memoId:메모가 수정되었습니다."
            verify(exactly = 1) { memoCommandService.updateMemo(memoId, 1L, newTitle, newContent) }
        }

        @Test
        fun `제목만 수정할 수 있다`() {
            // given
            val memoId = 123L
            val newTitle = "수정된 제목"
            val emptyContent = ""

            every { memoCommandService.updateMemo(memoId, 1L, newTitle, null) } returns memoId

            // when
            val result = memoTools.updateMemo(memoId, newTitle, emptyContent)

            // then
            result shouldBe "MEMO_UPDATED:$memoId:메모가 수정되었습니다."
            verify(exactly = 1) { memoCommandService.updateMemo(memoId, 1L, newTitle, null) }
        }

        @Test
        fun `내용만 수정할 수 있다`() {
            // given
            val memoId = 123L
            val emptyTitle = ""
            val newContent = "수정된 내용"

            every { memoCommandService.updateMemo(memoId, 1L, null, newContent) } returns memoId

            // when
            val result = memoTools.updateMemo(memoId, emptyTitle, newContent)

            // then
            result shouldBe "MEMO_UPDATED:$memoId:메모가 수정되었습니다."
            verify(exactly = 1) { memoCommandService.updateMemo(memoId, 1L, null, newContent) }
        }

        @Test
        fun `빈 문자열은 null로 변환되어 전달된다`() {
            // given
            val memoId = 123L
            val blankTitle = "   "
            val blankContent = "   "

            every { memoCommandService.updateMemo(memoId, 1L, null, null) } returns memoId

            // when
            val result = memoTools.updateMemo(memoId, blankTitle, blankContent)

            // then
            result shouldBe "MEMO_UPDATED:$memoId:메모가 수정되었습니다."
            verify(exactly = 1) { memoCommandService.updateMemo(memoId, 1L, null, null) }
        }
    }

    @Nested
    inner class DeleteMemoTests {
        @Test
        fun `메모 삭제에 성공하면 DELETED 메시지를 반환한다`() {
            // given
            val memoId = 123L
            val memoTitle = "삭제할 메모"
            val memoInfo = MemoCommandService.MemoInfo(memoId, memoTitle, "내용", null)

            every { memoCommandService.getMemoById(memoId) } returns memoInfo
            justRun { memoCommandService.deleteMemo(memoId, 1L) }

            // when
            val result = memoTools.deleteMemo(memoId)

            // then
            result shouldBe "DELETED:MEMO:$memoId:메모 '$memoTitle'이(가) 삭제되었습니다."
            verify(exactly = 1) { memoCommandService.getMemoById(memoId) }
            verify(exactly = 1) { memoCommandService.deleteMemo(memoId, 1L) }
        }

        @Test
        fun `존재하지 않는 메모를 삭제하면 에러 메시지를 반환한다`() {
            // given
            val memoId = 999L

            every { memoCommandService.getMemoById(memoId) } returns null

            // when
            val result = memoTools.deleteMemo(memoId)

            // then
            result shouldBe "ERROR:메모를 찾을 수 없습니다. (ID: $memoId)"
            verify(exactly = 1) { memoCommandService.getMemoById(memoId) }
            verify(exactly = 0) { memoCommandService.deleteMemo(any(), any()) }
        }
    }

    @Nested
    inner class MoveMemoToFolderTests {
        @Test
        fun `메모를 특정 폴더로 이동할 수 있다`() {
            // given
            val memoId = 123L
            val folderId = 456L

            justRun { memoCommandService.linkMemoToFolder(memoId, folderId, 1L) }

            // when
            val result = memoTools.moveMemoToFolder(memoId, folderId)

            // then
            result shouldBe "MEMO_MOVED:$memoId:$folderId:메모가 폴더 $folderId(으)로 이동되었습니다."
            verify(exactly = 1) { memoCommandService.linkMemoToFolder(memoId, folderId, 1L) }
        }

        @Test
        fun `메모를 루트로 이동할 수 있다`() {
            // given
            val memoId = 123L
            val rootFolderId = 0L

            justRun { memoCommandService.linkMemoToFolder(memoId, null, 1L) }

            // when
            val result = memoTools.moveMemoToFolder(memoId, rootFolderId)

            // then
            result shouldBe "MEMO_MOVED:$memoId:0:메모가 루트(으)로 이동되었습니다."
            verify(exactly = 1) { memoCommandService.linkMemoToFolder(memoId, null, 1L) }
        }
    }

    @Nested
    inner class ListMemosTests {
        @Test
        fun `메모 목록을 조회할 수 있다`() {
            // given
            val memos = listOf(
                MemoCommandService.MemoInfo(1L, "메모1", "내용1", null),
                MemoCommandService.MemoInfo(2L, "메모2", "내용2", 10L),
                MemoCommandService.MemoInfo(3L, "메모3", "내용3", null)
            )

            every { memoCommandService.getAllMemos(1L) } returns memos

            // when
            val result = memoTools.listMemos()

            // then
            result shouldStartWith "MEMO_LIST:3:"
            result shouldContain "- [1] 메모1"
            result shouldContain "- [2] 메모2 (폴더: 10)"
            result shouldContain "- [3] 메모3"
            verify(exactly = 1) { memoCommandService.getAllMemos(1L) }
        }

        @Test
        fun `메모가 없을 때 빈 목록 메시지를 반환한다`() {
            // given
            every { memoCommandService.getAllMemos(1L) } returns emptyList()

            // when
            val result = memoTools.listMemos()

            // then
            result shouldBe "MEMO_LIST:0:저장된 메모가 없습니다."
            verify(exactly = 1) { memoCommandService.getAllMemos(1L) }
        }
    }

    @Nested
    inner class CreateFolderTests {
        @Test
        fun `상위 폴더를 지정하여 폴더를 생성할 수 있다`() {
            // given
            val folderName = "새 폴더"
            val parentFolderId = 10L
            val newFolderId = 20L

            every { memoCommandService.createFolder(1L, folderName, parentFolderId) } returns newFolderId

            // when
            val result = memoTools.createFolder(folderName, parentFolderId)

            // then
            result shouldBe "FOLDER_CREATED:$newFolderId:$folderName:폴더 '$folderName'이(가) 생성되었습니다."
            verify(exactly = 1) { memoCommandService.createFolder(1L, folderName, parentFolderId) }
        }

        @Test
        fun `최상위에 폴더를 생성할 수 있다`() {
            // given
            val folderName = "최상위 폴더"
            val parentFolderId = 0L
            val newFolderId = 30L

            every { memoCommandService.createFolder(1L, folderName, null) } returns newFolderId

            // when
            val result = memoTools.createFolder(folderName, parentFolderId)

            // then
            result shouldBe "FOLDER_CREATED:$newFolderId:$folderName:폴더 '$folderName'이(가) 생성되었습니다."
            verify(exactly = 1) { memoCommandService.createFolder(1L, folderName, null) }
        }
    }

    @Nested
    inner class RenameFolderTests {
        @Test
        fun `폴더 이름을 변경할 수 있다`() {
            // given
            val folderId = 10L
            val newName = "변경된 폴더명"

            justRun { memoCommandService.renameFolder(folderId, 1L, newName) }

            // when
            val result = memoTools.renameFolder(folderId, newName)

            // then
            result shouldBe "FOLDER_RENAMED:$folderId:$newName:폴더 이름이 '$newName'(으)로 변경되었습니다."
            verify(exactly = 1) { memoCommandService.renameFolder(folderId, 1L, newName) }
        }
    }

    @Nested
    inner class DeleteFolderTests {
        @Test
        fun `폴더 삭제에 성공하면 DELETED 메시지를 반환한다`() {
            // given
            val folderId = 10L
            val folderName = "삭제할 폴더"
            val folders = listOf(
                MemoCommandService.FolderInfo(folderId, folderName, null)
            )

            every { memoCommandService.getAllFolders(1L) } returns folders
            justRun { memoCommandService.deleteFolder(folderId, 1L) }

            // when
            val result = memoTools.deleteFolder(folderId)

            // then
            result shouldBe "DELETED:FOLDER:$folderId:폴더 '$folderName'이(가) 삭제되었습니다. (하위 항목도 함께 삭제됨)"
            verify(exactly = 1) { memoCommandService.getAllFolders(1L) }
            verify(exactly = 1) { memoCommandService.deleteFolder(folderId, 1L) }
        }

        @Test
        fun `존재하지 않는 폴더를 삭제하면 에러 메시지를 반환한다`() {
            // given
            val folderId = 999L

            every { memoCommandService.getAllFolders(1L) } returns emptyList()

            // when
            val result = memoTools.deleteFolder(folderId)

            // then
            result shouldBe "ERROR:폴더를 찾을 수 없습니다. (ID: $folderId)"
            verify(exactly = 1) { memoCommandService.getAllFolders(1L) }
            verify(exactly = 0) { memoCommandService.deleteFolder(any(), any()) }
        }
    }

    @Nested
    inner class MoveFolderToParentTests {
        @Test
        fun `폴더를 다른 폴더로 이동할 수 있다`() {
            // given
            val folderId = 10L
            val parentFolderId = 20L

            justRun { memoCommandService.linkFolderToParent(folderId, parentFolderId, 1L) }

            // when
            val result = memoTools.moveFolderToParent(folderId, parentFolderId)

            // then
            result shouldBe "FOLDER_MOVED:$folderId:$parentFolderId:폴더가 폴더 $parentFolderId(으)로 이동되었습니다."
            verify(exactly = 1) { memoCommandService.linkFolderToParent(folderId, parentFolderId, 1L) }
        }

        @Test
        fun `폴더를 최상위로 이동할 수 있다`() {
            // given
            val folderId = 10L
            val rootFolderId = 0L

            justRun { memoCommandService.linkFolderToParent(folderId, null, 1L) }

            // when
            val result = memoTools.moveFolderToParent(folderId, rootFolderId)

            // then
            result shouldBe "FOLDER_MOVED:$folderId:0:폴더가 최상위(으)로 이동되었습니다."
            verify(exactly = 1) { memoCommandService.linkFolderToParent(folderId, null, 1L) }
        }
    }

    @Nested
    inner class ListFoldersTests {
        @Test
        fun `폴더 목록을 조회할 수 있다`() {
            // given
            val folders = listOf(
                MemoCommandService.FolderInfo(1L, "폴더1", null),
                MemoCommandService.FolderInfo(2L, "폴더2", 1L),
                MemoCommandService.FolderInfo(3L, "폴더3", null)
            )

            every { memoCommandService.getAllFolders(1L) } returns folders

            // when
            val result = memoTools.listFolders()

            // then
            result shouldStartWith "FOLDER_LIST:3:"
            result shouldContain "- [1] 폴더1 (최상위)"
            result shouldContain "- [2] 폴더2 (상위: 1)"
            result shouldContain "- [3] 폴더3 (최상위)"
            verify(exactly = 1) { memoCommandService.getAllFolders(1L) }
        }

        @Test
        fun `폴더가 없을 때 빈 목록 메시지를 반환한다`() {
            // given
            every { memoCommandService.getAllFolders(1L) } returns emptyList()

            // when
            val result = memoTools.listFolders()

            // then
            result shouldBe "FOLDER_LIST:0:생성된 폴더가 없습니다."
            verify(exactly = 1) { memoCommandService.getAllFolders(1L) }
        }
    }
}
