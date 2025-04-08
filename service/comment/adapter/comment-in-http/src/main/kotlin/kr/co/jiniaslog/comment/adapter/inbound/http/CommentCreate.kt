package kr.co.jiniaslog.comment.adapter.inbound.http

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.ReferenceId
import kr.co.jiniaslog.comment.usecase.ICreateComment

data class CommentCreate(
    val refId: Long,
    val refType: Comment.RefType,
    val userName: String?,
    val password: String?,
    val parentId: CommentId?,
    val content: String
) {
    fun toCommand(userId: Long?): ICreateComment.Command {
        return ICreateComment.Command(
            refId = ReferenceId(refId),
            refType = refType,
            userName = userName,
            password = password,
            parentId = parentId,
            content = content,
            userId = userId
        )
    }
}

data class CommentCreateResponse(
    val commentId: CommentId
)

fun ICreateComment.Info.toResponse() = CommentCreateResponse(
    commentId = this.commentId
)
