package kr.co.jiniaslog.article.application.port

import kr.co.jiniaslog.article.adapter.http.domain.ArticleId

interface ArticleIdGenerator {
    fun generate(): ArticleId
}
