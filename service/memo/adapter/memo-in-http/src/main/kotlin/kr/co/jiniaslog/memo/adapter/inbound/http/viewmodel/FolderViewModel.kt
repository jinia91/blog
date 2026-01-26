package kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel

data class FolderViewModel(
    val id: Long?,
    val name: String?,
    val parent: Long?,
    var children: List<FolderViewModel>,
    var memos: List<MemoViewModel>,
)
