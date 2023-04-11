package kr.co.jiniaslog.blogcore.application.article.infra

import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId

interface ArticleRepository {
    fun save(newArticle: Article)

    fun findById(articleId: ArticleId): Article?
}
