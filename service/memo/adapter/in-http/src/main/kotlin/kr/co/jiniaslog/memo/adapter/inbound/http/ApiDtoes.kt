package kr.co.jiniaslog.memo.adapter.inbound.http

import kr.co.jiniaslog.memo.queries.IGetAllMemos
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.impl.IGetMemoById

data class MemoResponse(
    val memos: List<IGetAllMemos.Info>,
)

fun IRecommendRelatedMemo.Info.toResponse(): MemoResponse {
    return MemoResponse(
        memos = this.relatedMemoCandidates.map { IGetAllMemos.Info(memoId = it.first, title = it.second, references = emptySet()) },
    )
}

fun List<IGetAllMemos.Info>.toResponse(): MemoResponse {
    return MemoResponse(
        memos = this,
    )
}

data class GetMemoByIdResponse(
    val memoId: Long,
    val title: String,
    val content: String,
    val references: Set<Reference>,
) {
    data class Reference(
        val rootId: Long,
        val referenceId: Long,
    )
}

fun IGetMemoById.Info.toResponse(): GetMemoByIdResponse {
    return GetMemoByIdResponse(
        memoId = this.memoId.value,
        title = this.title.value,
        content = this.content.value,
        references =
            this.references.map {
                GetMemoByIdResponse.Reference(
                    rootId = it.rootId.value,
                    referenceId = it.referenceId.value,
                )
            }.toSet(),
    )
}

data class DeleteMemoByIdResponse(
    val success: Boolean = true,
)

data class InitMemoRequest(
    val authorId: Long,
)

data class InitMemoResponse(
    val memoId: Long,
)
