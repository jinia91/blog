package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle

interface IGetRecommendRelatedMemo {
    fun getRecommendRelatedMemo(command: GetRecommendRelatedMemoCommand): GetRecommendRelatedMemoInfo

    data class GetRecommendRelatedMemoCommand(
        val query: String,
    )

    data class GetRecommendRelatedMemoInfo(
        val relatedMemoCandidates: List<Pair<MemoId, MemoTitle>>,
    )
}
