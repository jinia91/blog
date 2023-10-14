package kr.co.jiniaslog.blog.application.usecase

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId

interface ArticleQueries {
    fun findArticle(articleId: ArticleId): Article?
}
