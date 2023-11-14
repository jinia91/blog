package kr.co.jiniaslog.blog.adapter.inbound.message

import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import kr.co.jiniaslog.message.nexus.event.ArticleCommittedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import kr.co.jiniaslog.message.nexus.event.ArticleCreatedEventHandleable
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

private val log = mu.KotlinLogging.logger { }

@Controller
class ArticleEventsHandler(
//    private val articleQueries: ArticleQueries,
) : ArticleCreatedEventHandleable,
    ArticleCommittedEventHandleable {
    @ServiceActivator(inputChannel = ArticleCreatedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCreated) {}

    @ServiceActivator(inputChannel = ArticleCommittedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCommitted) {
        // todo : article view 모델 생성
    }
}
