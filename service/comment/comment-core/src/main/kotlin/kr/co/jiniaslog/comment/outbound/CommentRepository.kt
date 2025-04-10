package kr.co.jiniaslog.comment.outbound

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.ReferenceId
import kr.co.jiniaslog.shared.core.domain.Repository

interface CommentRepository : Repository<Comment, CommentId> {
    fun findByRefIdAndRefType(
        refId: ReferenceId,
        refType: Comment.RefType
    ): List<Comment>
}
