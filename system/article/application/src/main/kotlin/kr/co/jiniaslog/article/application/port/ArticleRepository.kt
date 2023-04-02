package kr.co.jiniaslog.article.application.port

import kr.co.jiniaslog.article.domain.Article
import kr.co.jiniaslog.article.domain.ArticleId

interface ArticleRepository {
    fun save(newArticle: Article)

    fun findById(articleId: ArticleId): Article?
}
