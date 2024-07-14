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

data class ArticleCategorizeResponse(
    val articleId: Long,
)

data class AddTagToArticleRequest(
    val tagName: String,
)

data class AddTagToArticleResponse(
    val articleId: Long,
)
