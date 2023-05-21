package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId

interface ArticleQueries {
    fun getArticle(articleId: ArticleId): Article?
}
