package kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName

data class FolderViewModel(
    val id: FolderId?,
    val name: FolderName?,
    val parent: FolderViewModel?,
    var children: List<FolderViewModel>,
    var memos: List<FolderViewModel>,
)
