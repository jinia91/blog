package kr.co.jiniaslog.blog.outbound

import kr.co.jiniaslog.blog.domain.article.ArticleVo

interface ArticleSearcher {
    fun searchPublishedArticlesByKeyword(keyword: String): List<ArticleVo>
}
