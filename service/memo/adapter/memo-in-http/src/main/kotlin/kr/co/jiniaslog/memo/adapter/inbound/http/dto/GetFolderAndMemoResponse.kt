package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchyByAuthorId

data class GetFolderAndMemoResponse(
    val folderInfos: List<IGetFoldersAllInHierirchyByAuthorId.FolderInfo>,
)

fun IGetFoldersAllInHierirchyByAuthorId.Info.toResponse(): GetFolderAndMemoResponse {
    return GetFolderAndMemoResponse(
        folderInfos = this.folderInfos,
    )
}
