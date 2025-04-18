package kr.co.jiniaslog.blog.adapter.inbound.http.dto

import java.time.LocalDateTime

data class GetArticleByIdResponse(
    val id: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val tags: List<TagViewModel>,
    val createdAt: LocalDateTime,
    val isPublished: Boolean
)
