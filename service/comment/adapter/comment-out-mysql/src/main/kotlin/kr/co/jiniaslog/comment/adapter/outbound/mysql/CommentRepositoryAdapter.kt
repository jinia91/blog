package kr.co.jiniaslog.comment.adapter.outbound.mysql

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.ReferenceId
import kr.co.jiniaslog.comment.outbound.CommentRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface CommentJpaRepository : JpaRepository<Comment, CommentId> {
    fun findByRefIdAndRefType(refId: ReferenceId, refType: Comment.RefType): List<Comment>
}

@Repository
class CommentRepositoryAdapter(
    private val commentJpaRepository: CommentJpaRepository,
) : CommentRepository {
    override fun findByRefIdAndRefType(refId: ReferenceId, refType: Comment.RefType): List<Comment> {
        return commentJpaRepository.findByRefIdAndRefType(refId, refType)
    }

    override fun findById(id: CommentId): Comment? {
        return commentJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: CommentId) {
        commentJpaRepository.deleteById(id)
    }

    override fun save(entity: Comment): Comment {
        return commentJpaRepository.save(entity)
    }
}
