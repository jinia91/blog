package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId

interface IReorderMemo {
    fun handle(command: Command): Info

    data class Command(
        val memoId: MemoId,
        val newSequence: String,
        val requesterId: AuthorId,
    )

    data class Info(
        val memoId: MemoId,
        val sequence: String,
    )
}
