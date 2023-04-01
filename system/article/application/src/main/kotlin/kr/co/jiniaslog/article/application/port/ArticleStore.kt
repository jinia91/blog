package kr.co.jiniaslog.article.application.port

import kr.co.jiniaslog.article.domain.Article
import java.util.*

interface ArticleStore {
    fun save(newArticle: Article)
    fun deleteArticle(articleId: Long)
}
