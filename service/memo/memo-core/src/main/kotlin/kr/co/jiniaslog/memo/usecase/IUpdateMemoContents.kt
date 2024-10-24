package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface IUpdateMemoContents {
    fun handle(command: Command): Info

    data class Command(
        val memoId: MemoId,
        val title: MemoTitle? = null,
        val content: MemoContent? = null,
    )

    data class Info(val id: MemoId)
}
