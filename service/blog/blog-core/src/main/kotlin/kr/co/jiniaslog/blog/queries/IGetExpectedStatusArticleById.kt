package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.TagId
import java.time.LocalDateTime

/**
 * 나는 해당 아티클의 아이디와 원하는 상태의 데이터를 가져올 수 있다.
 *
 * 상태로 필터링하는것이 아니다.
 *
 */
interface IGetExpectedStatusArticleById {
    fun handle(query: Query): Info

    data class Query(
        val articleId: ArticleId,
        val expectedStatus: Article.Status
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
