package kr.co.jiniaslog.comment.queries

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentVo
import kr.co.jiniaslog.comment.domain.ReferenceId

interface IGetHierarchyCommentsByRef {
    fun handle(command: Command): Info

    data class Command(
        val refId: ReferenceId,
        val refType: Comment.RefType
    )

    data class Info(
        val comments: List<CommentVo>
    )
}
