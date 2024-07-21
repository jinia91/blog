package kr.co.jiniaslog.blog.adapter.inbound.websocket

import jakarta.validation.Valid
import kr.co.jiniaslog.blog.adapter.inbound.websocket.payload.UpdateArticlePayload
import kr.co.jiniaslog.blog.adapter.inbound.websocket.payload.UpdateArticleResponse
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.blog.usecase.article.IUpdateArticleContents
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

private const val ARTICLE_PROTOCOL = "/topic/articleResponse"

@Controller
class ArticleWebSocketHandler(
    private val articleUseCasesFacade: ArticleUseCasesFacade
) {
    @MessageMapping("article/updateArticle")
    @SendTo(ARTICLE_PROTOCOL)
    fun handle(
        @Valid payload: UpdateArticlePayload,
    ): UpdateArticleResponse {
        val command: IUpdateArticleContents.Command = payload.toCommand()
        val info = articleUseCasesFacade.handle(command)
        return UpdateArticleResponse(info.articleId.value)
    }
}
