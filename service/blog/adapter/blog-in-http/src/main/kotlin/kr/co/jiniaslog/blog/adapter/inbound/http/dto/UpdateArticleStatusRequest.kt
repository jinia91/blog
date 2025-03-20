package kr.co.jiniaslog.blog.adapter.inbound.http.dto

import kr.co.jiniaslog.blog.domain.article.Article

data class UpdateArticleStatusRequest(
    val asIsStatus: Article.Status,
    val toBeStatus: Article.Status
)
