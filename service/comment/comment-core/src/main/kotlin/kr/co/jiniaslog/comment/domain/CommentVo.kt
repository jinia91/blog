package kr.co.jiniaslog.comment.domain

data class CommentVo(
    val id: Long,
    val content: String,
    val nickname: String,
    val authorId: Long?,
    val createdAt: String,
    val profileImageUrl: String?,
    val children: MutableList<CommentVo> = mutableListOf(),
    val deleted: Boolean,
) : Comparable<CommentVo> {
    companion object {
        fun from(comment: Comment): CommentVo {
            return CommentVo(
                id = comment.id.value,
                content = comment.contents.value,
                nickname = comment.authorInfo.authorName,
                createdAt = comment.createdAt.toString(),
                profileImageUrl = comment.authorInfo.profileImageUrl,
                authorId = comment.authorInfo.authorId,
                deleted = comment.status == Comment.Status.DELETED,
            )
        }
    }

    override fun compareTo(other: CommentVo): Int {
        return this.createdAt.compareTo(other.createdAt)
    }
}
