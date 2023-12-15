package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface IGetFoldersAll {
    fun handle(query: Query): Info

    class Query()

    data class Info(val folderInfos: List<FolderInfo>)
}

data class FolderInfo(
    val id: FolderId?,
    val name: FolderName?,
    val parent: FolderInfo?,
    var children: List<FolderInfo>,
    var memos: List<SimpleMemoInfo>,
)

data class SimpleMemoInfo(
    val memoId: MemoId,
    val title: MemoTitle,
    val references: List<MemoReferenceInfo>,
)

data class MemoReferenceInfo(
    val id: MemoId,
    val title: MemoTitle,
)
