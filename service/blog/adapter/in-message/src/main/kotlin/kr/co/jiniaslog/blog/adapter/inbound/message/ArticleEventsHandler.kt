package kr.co.jiniaslog.blog.adapter.inbound.message

import kr.co.jiniaslog.blog.usecase.ArticleUseCases
import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import kr.co.jiniaslog.message.nexus.event.ArticleCommittedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import kr.co.jiniaslog.message.nexus.event.ArticleCreatedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticleStagingSnapped
import kr.co.jiniaslog.message.nexus.event.ArticleStagingSnappedEventHandleable
import kr.co.jiniaslog.shared.adapter.inbound.messaging.withDomainContext
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

private val log = mu.KotlinLogging.logger { }

@Controller
class ArticleEventsHandler(
    private val articleUseCases: ArticleUseCases,
) : ArticleCreatedEventHandleable,
    ArticleCommittedEventHandleable,
    ArticleStagingSnappedEventHandleable {
    @ServiceActivator(inputChannel = ArticleCreatedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCreated) =
        withDomainContext {
            log.info { "article created event received : $event" }
        }

    @ServiceActivator(inputChannel = ArticleCommittedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCommitted) =
        withDomainContext {
            log.info { "article committed event received : $event" }
        }

    @ServiceActivator(inputChannel = ArticleStagingSnappedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleStagingSnapped) =
        withDomainContext {
            log.info { "article staging snapped event received : $event" }
        }
}
