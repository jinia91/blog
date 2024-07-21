package kr.co.jiniaslog.memo.adapter.inbound.http.dto

data class MakeFolderRelationshipRequest(
    val parentId: Long?,
)

data class MakeFolderRelationshipResponse(
    val parentId: Long?,
    val childId: Long,
)
