package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo

data class AddParentFolderRequest(
    val folderId: Long?,
)

data class AddParentFolderResponse(
    val memoId: Long,
    val folderId: Long?,
)

fun IMakeRelationShipFolderAndMemo.Info.toResponse(): AddParentFolderResponse {
    return AddParentFolderResponse(
        memoId = this.memoId.value,
        folderId = this.folderId?.value,
    )
}
