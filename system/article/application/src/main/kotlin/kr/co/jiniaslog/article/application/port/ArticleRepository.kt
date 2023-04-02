package kr.co.jiniaslog.article.application.port

import kr.co.jiniaslog.article.adapter.http.domain.Article
import kr.co.jiniaslog.article.adapter.http.domain.ArticleId

interface ArticleRepository {
    fun save(newArticle: Article)

    fun findById(articleId: ArticleId): Article?
}
