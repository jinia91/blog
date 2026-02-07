package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface ICreateMemoWithContent {
    fun handle(command: Command): Info

    data class Command(
        val authorId: AuthorId,
        val title: MemoTitle,
        val content: MemoContent,
        val parentFolderId: FolderId? = null,
    )

    data class Info(val memoId: MemoId)
}
