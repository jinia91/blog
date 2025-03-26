package kr.co.jiniaslog.blog.adapter.inbound.http.dto

import kr.co.jiniaslog.blog.domain.article.Article
import java.time.LocalDateTime

data class SimpleArticleCardsViewModel(
    val id: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val createdAt: LocalDateTime,
    val tags: Map<Long, String>,
    val contentStatus: Article.Status
)
