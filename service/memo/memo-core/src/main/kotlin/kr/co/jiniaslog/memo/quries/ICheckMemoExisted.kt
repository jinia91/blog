package kr.co.jiniaslog.memo.quries

import kr.co.jiniaslog.memo.domain.memo.MemoId

interface ICheckMemoExisted {
    fun handle(query: Query): Boolean

    data class Query(
        val memoId: MemoId,
    )
}
