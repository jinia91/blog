package kr.co.jiniaslog.comment.usecase

import kr.co.jiniaslog.comment.domain.CommentId

interface IDeleteComment {
    fun handle(command: Command): Info

    data class Command(
        val commentId: CommentId,
        val authorId: Long?,
        val password: String?,
    )

    data class Info(
        val commentId: CommentId,
    )
}
