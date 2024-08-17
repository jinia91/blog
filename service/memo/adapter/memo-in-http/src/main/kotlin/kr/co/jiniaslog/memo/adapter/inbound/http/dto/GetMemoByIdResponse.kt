package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.queries.IGetMemoById

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
