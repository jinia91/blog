package kr.co.jiniaslog.article.application.port

import kr.co.jiniaslog.article.domain.ArticleId

interface ArticleIdGenerator {
    fun generate(): ArticleId
}
