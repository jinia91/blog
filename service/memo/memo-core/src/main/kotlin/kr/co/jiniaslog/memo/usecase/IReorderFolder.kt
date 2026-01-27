package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId

interface IReorderFolder {
    fun handle(command: Command): Info

    data class Command(
        val folderId: FolderId,
        val newSequence: String,
        val requesterId: AuthorId,
    )

    data class Info(
        val folderId: FolderId,
        val sequence: String,
    )
}
