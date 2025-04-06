package kr.co.jiniaslog.comment.usecase

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.ReferenceId

interface ICreateComment {
    fun handle(command: Command): Info

    data class Command(
        val refId: ReferenceId,
        val refType: Comment.RefType,
        val userId: Long?,
        val userName: String?,
        val password: String?,
        val parentId: CommentId?,
        val content: String
    )

    data class Info(
        val commentId: CommentId,
    )
}
