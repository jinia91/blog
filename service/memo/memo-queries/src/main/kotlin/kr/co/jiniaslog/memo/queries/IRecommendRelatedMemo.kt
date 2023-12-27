package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface IRecommendRelatedMemo {
    fun handle(query: Query): Info

    data class Query(
        val query: String,
        val thisMemoId: MemoId,
    )

    data class Info(
        val relatedMemoCandidates: List<Triple<MemoId, MemoTitle, MemoContent>>,
    )
}
