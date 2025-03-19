package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.domain.article.ArticleId
import java.time.LocalDateTime

interface IGetPublishedSimpleArticleListWithCursor {
    fun handle(query: Query): List<Info>

    data class Query(
        val cursor: ArticleId,
        val limit: Int,
        val isPublished: Boolean
    )

    data class Info(
        val id: Long,
        val title: String,
        val content: String,
        val thumbnailUrl: String,
        val createdAt: LocalDateTime,
        val tags: Map<Long, String>
    )
}
