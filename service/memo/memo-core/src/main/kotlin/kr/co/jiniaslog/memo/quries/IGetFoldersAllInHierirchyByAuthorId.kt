package kr.co.jiniaslog.memo.quries

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface IGetFoldersAllInHierirchyByAuthorId {
    fun handle(query: Query): Info

    data class Query(
        val value: String?,
        val requesterId: AuthorId,
    )

    data class Info(val folderInfos: List<FolderInfo>)

    data class FolderInfo(
        val id: FolderId?,
        val name: FolderName?,
        val parent: FolderInfo?,
        var children: List<FolderInfo>,
        var memos: List<MemoInfo>,
    )

    data class MemoReferenceInfo(
        val id: MemoId,
        val title: MemoTitle,
    )

    class MemoInfo(
        val id: MemoId,
        val title: MemoTitle,
        val references: List<MemoReferenceInfo>,
    )
}
