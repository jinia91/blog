package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.shared.core.annotation.NoArgConstructor

interface IGetFoldersAllInHierirchyByAuthorId {
    fun handle(query: Query): Info

    data class Query(
        val value: String?,
        val requesterId: AuthorId,
    )

    @NoArgConstructor
    data class Info(val folderInfos: List<FolderInfo>)

    @NoArgConstructor
    data class FolderInfo(
        val id: Long?,
        val name: String?,
        val parent: FolderInfo?,
        var children: List<FolderInfo>,
        var memos: List<MemoInfo>,
    )

    @NoArgConstructor
    data class MemoReferenceInfo(
        val id: Long,
        val title: String,
    )

    @NoArgConstructor
    data class MemoInfo(
        val id: Long,
        val title: String,
        val references: List<MemoReferenceInfo>,
    )
}
