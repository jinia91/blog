package kr.co.jiniaslog.blog.adapter.inbound.message

import kr.co.jiniaslog.blog.domain.article.ArticleCommitVersion
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.writer.WriterId
import kr.co.jiniaslog.blog.usecase.ArticleUseCases
import kr.co.jiniaslog.blog.usecase.ArticleViewUpsertUseCase.ArticleViewUpsertCommand
import kr.co.jiniaslog.blog.usecase.ArticleViewUseCases
import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import kr.co.jiniaslog.message.nexus.event.ArticleCommittedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import kr.co.jiniaslog.message.nexus.event.ArticleCreatedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticleDeleted
import kr.co.jiniaslog.message.nexus.event.ArticleDeletedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticlePublished
import kr.co.jiniaslog.message.nexus.event.ArticlePublishedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticleStagingSnapped
import kr.co.jiniaslog.message.nexus.event.ArticleStagingSnappedEventHandleable
import kr.co.jiniaslog.shared.core.domain.withDomainContext
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

private val log = mu.KotlinLogging.logger { }

@Controller
class ArticleEventsHandler(
    private val articleUseCases: ArticleUseCases,
    private val articleViewUseCases: ArticleViewUseCases,
) : ArticleCreatedEventHandleable,
    ArticleCommittedEventHandleable,
    ArticleStagingSnappedEventHandleable,
    ArticleDeletedEventHandleable,
    ArticlePublishedEventHandleable {
    @ServiceActivator(inputChannel = ArticleCreatedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCreated) =
        withDomainContext {
            log.debug { "article created event received : $event" }
        }

    @ServiceActivator(inputChannel = ArticleCommittedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCommitted) =
        withDomainContext {
            log.debug { "article committed event received : $event" }
            articleViewUseCases.upsert(
                ArticleViewUpsertCommand(
                    articleId = ArticleId(event.articleId),
                    writerId = WriterId(event.writerId),
                    categoryId = event.categoryId?.let { CategoryId(it) },
                    headVersion = ArticleCommitVersion(event.head),
                ),
            )
        }

    @ServiceActivator(inputChannel = ArticleStagingSnappedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleStagingSnapped) =
        withDomainContext {
            log.debug { "article staging snapped event received : $event" }
        }

    @ServiceActivator(inputChannel = ArticleDeletedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleDeleted) =
        withDomainContext {
            log.debug { "article deleted event received : $event" }
            // todo view delete
        }

    @ServiceActivator(inputChannel = ArticlePublishedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticlePublished) =
        withDomainContext {
            log.debug { "article published event received : $event" }
            articleViewUseCases.upsert(
                ArticleViewUpsertCommand(
                    articleId = ArticleId(event.articleId),
                    writerId = WriterId(event.writerId),
                    categoryId = CategoryId(event.categoryId),
                    headVersion = ArticleCommitVersion(event.head),
                ),
            )
        }
}
