package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.usecase.IEditArticleContents

data class ArticlePostResponse(
    val articleId: Long,
)

data class ArticleUpdateRequest(
    val categoryId: Long,
    val refMemoId: Long,
    val title: String,
    val contents: String,
    val thumbnailUrl: String,
    val tags: List<Long>,
) {
    fun toCommand(articleId: Long): IEditArticleContents.Command {
        return IEditArticleContents.Command(
            articleId = ArticleId(articleId),
            articleContents =
            ArticleContents(
                title = title,
                contents = contents,
                thumbnailUrl = thumbnailUrl,
            )
        )
    }
}

data class ArticleUpdateResponse(
    val articleId: Long,
)

data class ArticleDeleteResponse(
    val articleId: Long,
)
