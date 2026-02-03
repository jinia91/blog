package kr.co.jiniaslog.ai.usecase

interface IRecommendRelatedMemos {
    data class Query(
        val authorId: Long,
        val currentMemoId: Long? = null,
        val query: String? = null,
        val topK: Int = 5,
    )

    data class RecommendedMemo(
        val memoId: Long,
        val title: String,
        val contentPreview: String,
        val similarity: Double,
    )

    operator fun invoke(query: Query): List<RecommendedMemo>
}
