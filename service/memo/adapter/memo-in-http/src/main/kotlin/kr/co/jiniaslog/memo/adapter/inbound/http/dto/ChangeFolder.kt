package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.usecase.IChangeFolderName

data class ChangeFolderNameRequest(
    val folderId: Long,
    val name: String,
) {
    fun toCommand(): IChangeFolderName.Command {
        return IChangeFolderName.Command(
            folderId = FolderId(folderId),
            name = FolderName(name),
        )
    }
}

data class ChangeFolderNameResponse(
    val folderId: Long,
)
