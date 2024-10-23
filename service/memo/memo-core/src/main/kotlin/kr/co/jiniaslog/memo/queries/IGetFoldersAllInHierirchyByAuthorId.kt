package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.memo.AuthorId

interface IGetFoldersAllInHierirchyByAuthorId {
    fun handle(query: Query): Info

    data class Query(
        val value: String?,
        val requesterId: AuthorId,
    )

    class Info(val folderInfos: List<FolderInfo>)

    class FolderInfo(
        val id: Long?,
        val name: String?,
        val parent: FolderInfo?,
        var children: List<FolderInfo>,
        var memos: List<MemoInfo>,
    )
    data class MemoReferenceInfo(
        val id: Long,
        val title: String,
    )

    data class MemoInfo(
        val id: Long,
        val title: String,
        val references: List<MemoReferenceInfo>,
    )
}
