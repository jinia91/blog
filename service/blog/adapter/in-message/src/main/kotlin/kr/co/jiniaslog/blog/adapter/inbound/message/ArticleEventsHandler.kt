package kr.co.jiniaslog.blog.adapter.inbound.message

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.usecase.ArticleStagingCommandUseCase
import kr.co.jiniaslog.blog.usecase.ArticleUseCases
import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import kr.co.jiniaslog.message.nexus.event.ArticleCommittedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import kr.co.jiniaslog.message.nexus.event.ArticleCreatedEventHandleable
import kr.co.jiniaslog.shared.adapter.inbound.messaging.withDomainContext
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

private val log = mu.KotlinLogging.logger { }

@Controller
class ArticleEventsHandler(
    private val articleUseCases: ArticleUseCases,
) : ArticleCreatedEventHandleable,
    ArticleCommittedEventHandleable {
    @ServiceActivator(inputChannel = ArticleCreatedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCreated) =
        withDomainContext {
            articleUseCases.staging(
                ArticleStagingCommandUseCase.ArticleStagingCommand(
                    articleId = ArticleId(event.articleId),
                    null,
                    null,
                    null,
                    null,
                ),
            )
        }

    @ServiceActivator(inputChannel = ArticleCommittedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCommitted) =
        withDomainContext {
            // todo : article view 모델 생성
        }
}
