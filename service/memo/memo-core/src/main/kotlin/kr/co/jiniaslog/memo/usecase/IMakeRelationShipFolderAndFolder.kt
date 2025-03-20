package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId

interface IMakeRelationShipFolderAndFolder {
    fun handle(command: Command): Info

    data class Command(
        val parentFolderId: FolderId?,
        val childFolderId: FolderId,
        val requesterId: AuthorId
    )

    data class Info(val parentFolderId: FolderId?, val childFolderId: FolderId)
}
