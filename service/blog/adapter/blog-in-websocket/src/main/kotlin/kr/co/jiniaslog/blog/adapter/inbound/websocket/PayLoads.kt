package kr.co.jiniaslog.blog.adapter.inbound.websocket

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.usecase.article.IUpdateArticleContents

data class UpdateArticlePayload(
    val type: String = "UpdateArticle",
    val articleId: Long,
    val content: String,
    val title: String,
    val thumbnailUrl: String,
) {
    fun toCommand(): IUpdateArticleContents.Command {
        return IUpdateArticleContents.Command(
            articleId = ArticleId(articleId),
            articleContents = ArticleContents(content, title, thumbnailUrl),
        )
    }
}

class UpdateArticleResponse @JsonCreator constructor(
    @JsonProperty("id")
    val id: Long,
)
