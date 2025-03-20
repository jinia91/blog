package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo

data class GetAllReferencedByMemoResponse(
    val referenceds: List<Referenced>,
) {
    data class Referenced(
        val id: Long,
        val title: String,
    )
}

fun IGetAllReferencedByMemo.Info.toResponse(): GetAllReferencedByMemoResponse {
    return GetAllReferencedByMemoResponse(
        referenceds =
        this.referenceds.map {
            GetAllReferencedByMemoResponse.Referenced(
                id = it.id.value,
                title = it.title.value,
            )
        }.toList(),
    )
}
