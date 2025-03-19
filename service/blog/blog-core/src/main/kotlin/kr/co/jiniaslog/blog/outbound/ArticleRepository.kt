package kr.co.jiniaslog.blog.outbound

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.queries.IGetSimpleArticleListWithCursor
import kr.co.jiniaslog.shared.core.domain.Repository

interface ArticleRepository : Repository<Article, ArticleId> {
    fun getSimpleArticleListWithCursor(cursor: ArticleId, limit: Int): List<IGetSimpleArticleListWithCursor.Info>
}
