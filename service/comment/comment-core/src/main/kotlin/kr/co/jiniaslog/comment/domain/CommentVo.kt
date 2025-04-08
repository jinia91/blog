package kr.co.jiniaslog.comment.domain

data class CommentVo(
    val id: CommentId,
    val content: String,
    val nickname: String,
    val authorId: Long?,
    val createdAt: String,
    val profileImageUrl: String?,
    val children: MutableList<CommentVo> = mutableListOf()
) : Comparable<CommentVo> {
    companion object {
        fun from(comment: Comment): CommentVo {
            return CommentVo(
                id = comment.id,
                content = comment.contents.value,
                nickname = comment.authorInfo.authorName,
                createdAt = comment.createdAt.toString(),
                profileImageUrl = comment.authorInfo.profileImageUrl,
                authorId = comment.authorInfo.authorId,
            )
        }
    }

    override fun compareTo(other: CommentVo): Int {
        return this.createdAt.compareTo(other.createdAt)
    }
}
