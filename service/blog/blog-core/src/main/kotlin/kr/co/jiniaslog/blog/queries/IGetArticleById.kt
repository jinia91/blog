package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleVo

interface IGetArticleById {
    fun handle(query: Query): Info

    data class Query(
        val articleId: ArticleId,
    )

    data class Info(
        val article: ArticleVo
    )
}
