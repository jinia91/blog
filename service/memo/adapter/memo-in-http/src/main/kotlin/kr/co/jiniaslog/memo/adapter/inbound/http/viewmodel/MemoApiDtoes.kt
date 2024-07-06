package kr.co.jiniaslog.memo.adapter.inbound.http.viewmodel

import kr.co.jiniaslog.memo.adapter.inbound.http.AddParentFolderResponse
import kr.co.jiniaslog.memo.queries.IGetAllReferencedByMemo
import kr.co.jiniaslog.memo.queries.IGetAllReferencesByMemo
import kr.co.jiniaslog.memo.queries.IGetMemoById
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.usecase.IMakeRelationShipFolderAndMemo

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

fun IMakeRelationShipFolderAndMemo.Info.toResponse(): AddParentFolderResponse {
    return AddParentFolderResponse(
        memoId = this.memoId.value,
        folderId = this.folderId?.value,
    )
}

data class AddParentFolderRequest(
    val folderId: Long?,
)
