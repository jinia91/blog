package kr.co.jiniaslog.memo.adapter.inbound.http.dto

data class ReorderFolderResponse(
    val folderId: Long,
    val sequence: String,
)
