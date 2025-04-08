package kr.co.jiniaslog.comment.adapter.inbound.http.dto

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.ReferenceId
import kr.co.jiniaslog.comment.usecase.ICreateComment

data class CreateCommentRequest(
    val refId: Long,
    val refType: Comment.RefType,
    val userName: String?,
    val password: String?,
    val parentId: Long?,
    val content: String
) {
    fun toCommand(userId: Long?): ICreateComment.Command {
        return ICreateComment.Command(
            refId = ReferenceId(refId),
            refType = refType,
            userName = userName,
            password = password,
            parentId = parentId?.let { CommentId(parentId) },
            content = content,
            userId = userId
        )
    }
}

data class CreateCommentResponse(
    val commentId: Long
)

fun ICreateComment.Info.toResponse() = CreateCommentResponse(
    commentId = this.commentId.value
)
