package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId

interface ArticleRepository {
    fun save(newArticle: Article)

    fun update(article: Article)

    fun findById(articleId: ArticleId): Article?

    fun delete(articleId: ArticleId)

    fun findAll(): List<Article>
}
