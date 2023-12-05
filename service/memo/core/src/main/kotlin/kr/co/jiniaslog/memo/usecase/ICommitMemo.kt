package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface ICommitMemo {
    fun handle(command: Command): Info

    data class Command(
        val memoId: MemoId,
        val title: MemoTitle,
        val content: MemoContent,
    )

    data class Info(val id: MemoId)
}
