package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.folder.FolderName
import kr.co.jiniaslog.memo.domain.memo.AuthorId

interface ICreateNewFolder {
    fun handle(command: Command): Info

    data class Command(
        val authorId: AuthorId,
        val parentId: FolderId? = null,
    )

    data class Info(val id: FolderId, val folderName: FolderName, val parentId: FolderId?)
}
