package kr.co.jiniaslog.blog.outbound

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleVo
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.shared.core.domain.Repository

interface ArticleRepository : Repository<Article, ArticleId> {
    fun getArticleListWithCursor(
        cursor: Long,
        limit: Int,
        published: Boolean
    ): List<ArticleVo>

    fun getArticleByTagName(tagName: TagName): List<ArticleVo>

    fun findArticleVoByStatus(status: Article.Status): List<ArticleVo>
}
