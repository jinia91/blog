package kr.co.jiniaslog.comment.adapter.inbound.http

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.ReferenceId
import kr.co.jiniaslog.comment.usecase.ICreateComment

data class CommentCreate(
    val refId: ReferenceId,
    val refType: Comment.RefType,
    val userId: Long?,
    val userName: String?,
    val password: String?,
    val parentId: CommentId?,
    val content: String
) {
    fun toCommand(): ICreateComment.Command {
        return ICreateComment.Command(
            refId = refId,
            refType = refType,
            userId = userId,
            userName = userName,
            password = password,
            parentId = parentId,
            content = content
        )
    }
}

data class CommentCreateResponse(
    val commentId: CommentId
)

fun ICreateComment.Info.toResponse() = CommentCreateResponse(
    commentId = this.commentId
)
