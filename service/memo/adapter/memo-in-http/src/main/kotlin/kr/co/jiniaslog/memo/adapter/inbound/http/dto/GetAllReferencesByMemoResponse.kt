package kr.co.jiniaslog.memo.adapter.inbound.http.dto

import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo

data class GetAllReferencesByMemoResponse(
    val references: List<Reference>,
) {
    data class Reference(
        val id: Long,
        val title: String,
    )
}

fun IGetAllReferencesByMemo.Info.toResponse(): GetAllReferencesByMemoResponse {
    return GetAllReferencesByMemoResponse(
        references =
        this.references.map {
            GetAllReferencesByMemoResponse.Reference(
                id = it.id.value,
                title = it.title.value,
            )
        }.toList(),
    )
}
