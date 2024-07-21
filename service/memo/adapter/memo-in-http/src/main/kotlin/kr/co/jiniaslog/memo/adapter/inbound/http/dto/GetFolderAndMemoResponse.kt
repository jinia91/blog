package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.FolderViewModel
import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.toVm
import kr.co.jiniaslog.memo.queries.IGetFoldersAllInHierirchy

data class GetFolderAndMemoResponse(
    val folderInfos: List<FolderViewModel>,
)

fun IGetFoldersAllInHierirchy.Info.toResponse(): GetFolderAndMemoResponse {
    return GetFolderAndMemoResponse(
        folderInfos = this.folderInfos.map { it.toVm() }
    )
}
