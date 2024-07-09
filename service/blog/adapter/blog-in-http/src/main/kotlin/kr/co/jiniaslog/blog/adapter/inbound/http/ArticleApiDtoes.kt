package kr.co.jiniaslog.blog.adapter.inbound.http

data class ArticlePostResponse(
    val articleId: Long,
)

data class ArticlePublishResponse(
    val articleId: Long,
)

data class ArticleDeleteResponse(
    val articleId: Long,
)

data class AunDeleteArticleResponse(
    val articleId: Long,
)
