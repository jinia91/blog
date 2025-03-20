package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.TagId
import java.time.LocalDateTime

interface IGetArticleById {
    fun handle(query: Query): Info

    data class Query(
        val articleId: ArticleId,
        val isDraft: Boolean
    )

    data class Info(
        val id: ArticleId,
        val title: String,
        val content: String,
        val thumbnailUrl: String,
        val tags: Map<TagId, String>,
        val createdAt: LocalDateTime,
        val isPublished: Boolean
    )
}
