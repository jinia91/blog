package kr.co.jiniaslog.blogcore.application.article.infra

import kr.co.jiniaslog.blogcore.domain.article.ArticleId

interface ArticleIdGenerator {
    fun generate(): ArticleId
}
