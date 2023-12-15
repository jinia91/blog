package kr.co.jiniaslog.memo.queries.model

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName

data class FolderInfo(
    val id: FolderId?,
    val name: FolderName?,
    val parent: FolderInfo?,
    var children: List<FolderInfo>,
    var memos: List<SimpleMemoInfo>,
)
