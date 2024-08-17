package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel.MemoViewModel
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo

data class RecommendRelatedMemoResponse(
    val memos: List<MemoViewModel>,
)

fun IRecommendRelatedMemo.Info.toResponse(): RecommendRelatedMemoResponse {
    return RecommendRelatedMemoResponse(
        memos =
        this.relatedMemoCandidates.map {
            MemoViewModel(
                memoId = it.first.value,
                title = it.second.value,
                content = it.third.value,
            )
        },
    )
}
