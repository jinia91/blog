package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchy

data class GetFolderAndMemoResponse(
    val folderInfos: List<IGetFoldersAllInHierirchy.FolderInfo>,
)

fun IGetFoldersAllInHierirchy.Info.toResponse(): GetFolderAndMemoResponse {
    return GetFolderAndMemoResponse(
        folderInfos = this.folderInfos,
    )
}
