package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId

interface IMakeRelationShipFolderAndMemo {
    fun handle(command: Command): Info

    data class Command(
        val memoId: MemoId,
        val folderId: FolderId?,
        val requesterId: AuthorId,
    )

    data class Info(
        val memoId: MemoId,
        val folderId: FolderId?,
    )
}
