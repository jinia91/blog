package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.article.ArticleId

interface ArticleIdGenerator {
    fun generate(): ArticleId
}
