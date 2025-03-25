package kr.co.jiniaslog.blog.outbound

import kr.co.jiniaslog.blog.domain.article.PublishedArticleVo

interface ArticleSearcher {
    fun searchPublishedArticlesByKeyword(keyword: String): List<PublishedArticleVo>
}
