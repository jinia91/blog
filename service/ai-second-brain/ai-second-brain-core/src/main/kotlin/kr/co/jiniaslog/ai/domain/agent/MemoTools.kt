package kr.co.jiniaslog.ai.domain.agent

import kr.co.jiniaslog.ai.outbound.MemoCommandService
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Component

/**
 * Memo/Folder Agent에서 사용하는 Tool 정의
 * Spring AI의 Tool Calling 기능을 사용하여 메모와 폴더를 관리합니다.
 *
 * 삭제 작업: 에이전트 프롬프트에서 사용자 확인을 받도록 지시합니다.
 */
@Component
class MemoTools(
    private val memoCommandService: MemoCommandService
) {
    private var currentAuthorId: Long = 0

    fun setAuthorId(authorId: Long) {
        this.currentAuthorId = authorId
    }

    @Tool(description = "사용자의 메모를 생성합니다. 적절한 제목과 내용을 추출하여 저장합니다.")
    fun createMemo(
        @ToolParam(description = "메모 제목 (간결하고 명확하게, 최대 50자)") title: String,
        @ToolParam(description = "메모 내용") content: String
    ): String {
        val memoId = memoCommandService.createMemo(
            authorId = currentAuthorId,
            title = title,
            content = content
        )
        return "MEMO_CREATED:$memoId:$title:메모가 생성되었습니다."
    }

    @Tool(description = "기존 메모의 제목이나 내용을 수정합니다.")
    fun updateMemo(
        @ToolParam(description = "수정할 메모의 ID") memoId: Long,
        @ToolParam(description = "새로운 메모 제목 (변경하지 않으려면 빈 문자열)") title: String,
        @ToolParam(description = "새로운 메모 내용 (변경하지 않으려면 빈 문자열)") content: String
    ): String {
        memoCommandService.updateMemo(
            memoId = memoId,
            requesterId = currentAuthorId,
            title = title.takeIf { it.isNotBlank() },
            content = content.takeIf { it.isNotBlank() }
        )
        return "MEMO_UPDATED:$memoId:메모가 수정되었습니다."
    }

    @Tool(description = "메모를 삭제합니다. 주의: 이 도구를 호출하기 전에 반드시 사용자에게 확인을 받아야 합니다.")
    fun deleteMemo(
        @ToolParam(description = "삭제할 메모의 ID") memoId: Long
    ): String {
        val memo = memoCommandService.getMemoById(memoId)
            ?: return "ERROR:메모를 찾을 수 없습니다. (ID: $memoId)"

        memoCommandService.deleteMemo(memoId, currentAuthorId)
        return "DELETED:MEMO:$memoId:메모 '${memo.title}'이(가) 삭제되었습니다."
    }

    @Tool(description = "메모를 특정 폴더로 이동하거나 폴더에서 분리합니다.")
    fun moveMemoToFolder(
        @ToolParam(description = "이동할 메모의 ID") memoId: Long,
        @ToolParam(description = "이동할 폴더의 ID (루트로 이동하려면 0)") folderId: Long
    ): String {
        val targetFolderId = if (folderId == 0L) null else folderId
        memoCommandService.linkMemoToFolder(
            memoId = memoId,
            folderId = targetFolderId,
            requesterId = currentAuthorId
        )
        val folderName = if (targetFolderId == null) "루트" else "폴더 $folderId"
        return "MEMO_MOVED:$memoId:${targetFolderId ?: 0}:메모가 $folderName(으)로 이동되었습니다."
    }

    @Tool(description = "사용자의 모든 메모 목록을 조회합니다.")
    fun listMemos(): String {
        val memos = memoCommandService.getAllMemos(currentAuthorId)
        if (memos.isEmpty()) {
            return "MEMO_LIST:0:저장된 메모가 없습니다."
        }

        val memoListStr = memos.joinToString("\n") { memo ->
            "- [${memo.id}] ${memo.title}${memo.folderId?.let { " (폴더: $it)" } ?: ""}"
        }
        return "MEMO_LIST:${memos.size}:$memoListStr"
    }

    @Tool(description = "새로운 폴더를 생성합니다.")
    fun createFolder(
        @ToolParam(description = "폴더 이름") name: String,
        @ToolParam(description = "상위 폴더 ID (최상위에 생성하려면 0)") parentFolderId: Long
    ): String {
        val parentId = if (parentFolderId == 0L) null else parentFolderId
        val folderId = memoCommandService.createFolder(
            authorId = currentAuthorId,
            name = name,
            parentFolderId = parentId
        )
        return "FOLDER_CREATED:$folderId:$name:폴더 '$name'이(가) 생성되었습니다."
    }

    @Tool(description = "폴더의 이름을 변경합니다.")
    fun renameFolder(
        @ToolParam(description = "이름을 변경할 폴더의 ID") folderId: Long,
        @ToolParam(description = "새로운 폴더 이름") newName: String
    ): String {
        memoCommandService.renameFolder(
            folderId = folderId,
            requesterId = currentAuthorId,
            name = newName
        )
        return "FOLDER_RENAMED:$folderId:$newName:폴더 이름이 '$newName'(으)로 변경되었습니다."
    }

    @Tool(description = "폴더와 그 안의 모든 내용을 삭제합니다. 주의: 이 도구를 호출하기 전에 반드시 사용자에게 확인을 받아야 합니다.")
    fun deleteFolder(
        @ToolParam(description = "삭제할 폴더의 ID") folderId: Long
    ): String {
        val folders = memoCommandService.getAllFolders(currentAuthorId)
        val folder = folders.find { it.id == folderId }
            ?: return "ERROR:폴더를 찾을 수 없습니다. (ID: $folderId)"

        memoCommandService.deleteFolder(folderId, currentAuthorId)
        return "DELETED:FOLDER:$folderId:폴더 '${folder.name}'이(가) 삭제되었습니다. (하위 항목도 함께 삭제됨)"
    }

    @Tool(description = "폴더를 다른 폴더 안으로 이동합니다.")
    fun moveFolderToParent(
        @ToolParam(description = "이동할 폴더의 ID") folderId: Long,
        @ToolParam(description = "이동할 상위 폴더의 ID (최상위로 이동하려면 0)") parentFolderId: Long
    ): String {
        val parentId = if (parentFolderId == 0L) null else parentFolderId
        memoCommandService.linkFolderToParent(
            childFolderId = folderId,
            parentFolderId = parentId,
            requesterId = currentAuthorId
        )
        val parentName = if (parentId == null) "최상위" else "폴더 $parentId"
        return "FOLDER_MOVED:$folderId:${parentId ?: 0}:폴더가 $parentName(으)로 이동되었습니다."
    }

    @Tool(description = "사용자의 모든 폴더 목록을 조회합니다.")
    fun listFolders(): String {
        val folders = memoCommandService.getAllFolders(currentAuthorId)
        if (folders.isEmpty()) {
            return "FOLDER_LIST:0:생성된 폴더가 없습니다."
        }

        val folderListStr = folders.joinToString("\n") { folder ->
            "- [${folder.id}] ${folder.name}${folder.parentFolderId?.let { " (상위: $it)" } ?: " (최상위)"}"
        }
        return "FOLDER_LIST:${folders.size}:$folderListStr"
    }
}
