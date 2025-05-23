package kr.co.jiniaslog.comment.domain

import jakarta.persistence.Embeddable

@Embeddable
data class CommentContents(
    val value: String,
) {
    init {
        validate()
    }

    private fun validate() {
        require(value.isNotBlank()) { "Comment contents must not be blank" }
        require(value.length <= 1000) { "Comment contents must be less than 1000 characters" }
    }
}
