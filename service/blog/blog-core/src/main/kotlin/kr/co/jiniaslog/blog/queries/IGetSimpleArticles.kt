package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.domain.article.ArticleVo

interface IGetSimpleArticles {
    fun handle(query: Query): Info

    data class Query(
        val cursor: Long?,
        val limit: Int?,
        val isPublished: Boolean,
        val keyword: String?
    ) {
        fun isCursorQuery(): Boolean = cursor != null && limit != null
        fun isKeywordQuery(): Boolean = keyword != null
    }

    data class Info(
        val articles: List<ArticleVo>
    )
}
