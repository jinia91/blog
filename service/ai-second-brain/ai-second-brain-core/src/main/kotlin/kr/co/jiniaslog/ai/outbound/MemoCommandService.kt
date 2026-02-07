package kr.co.jiniaslog.ai.outbound

/**
 * 메모/폴더 관리를 위한 Command Service 인터페이스
 * AI Agent에서 Tool Calling을 통해 사용합니다.
 */
interface MemoCommandService {
    fun createMemo(authorId: Long, title: String, content: String): Long
    fun updateMemo(memoId: Long, requesterId: Long, title: String?, content: String?): Long
    fun deleteMemo(memoId: Long, requesterId: Long)
    fun linkMemoToFolder(memoId: Long, folderId: Long?, requesterId: Long)
    fun createFolder(authorId: Long, name: String, parentFolderId: Long?): Long
    fun renameFolder(folderId: Long, requesterId: Long, name: String)
    fun deleteFolder(folderId: Long, requesterId: Long)
    fun linkFolderToParent(childFolderId: Long, parentFolderId: Long?, requesterId: Long)
    fun getMemoById(memoId: Long): MemoInfo?
    fun getAllMemos(authorId: Long): List<MemoInfo>
    fun getAllFolders(authorId: Long): List<FolderInfo>

    data class MemoInfo(
        val id: Long,
        val title: String,
        val content: String,
        val folderId: Long?
    )

    data class FolderInfo(
        val id: Long,
        val name: String,
        val parentFolderId: Long?
    )
}
