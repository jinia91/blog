package kr.co.jiniaslog.comment.adapter.inbound.http.dto

data class DeleteCommentRequest(
    val password: String?,
)

data class DeleteCommentResponse(
    val commentId: Long,
)
