package kr.co.jiniaslog.comment.domain

data class CommentVo(
    val id: CommentId,
    val content: String,
    val author: String,
    val createdAt: String,
    val updatedAt: String,
    val children: MutableList<CommentVo> = mutableListOf()
) : Comparable<CommentVo> {
    companion object {
        fun from(comment: Comment): CommentVo {
            return CommentVo(
                id = comment.id,
                content = comment.contents.value,
                author = comment.authorInfo.authorName,
                createdAt = comment.createdAt.toString(),
                updatedAt = comment.updatedAt.toString()
            )
        }
    }

    override fun compareTo(other: CommentVo): Int {
        return this.createdAt.compareTo(other.createdAt)
    }
}
