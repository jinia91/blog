package kr.co.jiniaslog.comment.domain

import jakarta.persistence.Embeddable

@Embeddable
data class CommentContents(
    val contents: String,
) {
    init {
        validate()
    }

    private fun validate() {
        require(contents.isNotBlank()) { "Comment contents must not be blank" }
        require(contents.length <= 1000) { "Comment contents must be less than 1000 characters" }
    }
}
