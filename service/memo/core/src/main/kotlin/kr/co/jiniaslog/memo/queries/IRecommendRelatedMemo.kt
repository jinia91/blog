package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface IRecommendRelatedMemo {
    fun handle(query: Query): Info

    data class Query(
        val query: String,
        val thisId: MemoId,
    )

    data class Info(
        val relatedMemoCandidates: List<Pair<MemoId, MemoTitle>>,
    )
}
