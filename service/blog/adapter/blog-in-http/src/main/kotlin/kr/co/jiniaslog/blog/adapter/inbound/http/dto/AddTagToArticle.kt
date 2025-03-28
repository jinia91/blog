package kr.co.jiniaslog.blog.adapter.inbound.http.dto

data class AddTagToArticleRequest(
    val tagName: String,
)

data class AddTagToArticleResponse(
    val tagId: Long
)
