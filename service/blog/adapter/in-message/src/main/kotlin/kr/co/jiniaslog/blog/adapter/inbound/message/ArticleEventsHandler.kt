package kr.co.jiniaslog.blog.adapter.inbound.message

import kr.co.jiniaslog.message.nexus.event.AbstractArticleCreatedChannelHandler
import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

@Controller
class ArticleEventsHandler :
    AbstractArticleCreatedChannelHandler() {
    @ServiceActivator(inputChannel = CHANNEL_NAME)
    override suspend fun handle(event: ArticleCreated) {
        TODO("Not yet implemented")
    }
}
