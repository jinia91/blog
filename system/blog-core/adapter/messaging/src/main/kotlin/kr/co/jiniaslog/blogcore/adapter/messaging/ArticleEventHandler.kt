package kr.co.jiniaslog.blogcore.adapter.messaging

import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.DeleteDraftArticleCommand
import kr.co.jiniaslog.blogcore.domain.article.PublishedArticleCreatedEvent
import mu.KotlinLogging
import org.springframework.integration.annotation.IdempotentReceiver
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

private val log = KotlinLogging.logger { }

@Controller
class ArticleEventHandler(
    private val draftArticleCommands: DraftArticleCommands,
) {
    @ServiceActivator(inputChannel = "PublishedArticleCreatedEventChannel")
    @IdempotentReceiver("localIdempotentReceiverInterceptor")
    fun articleCreatedEventHandler(event: PublishedArticleCreatedEvent) = with(event) {
        log.info { "listen Event : $this" }
        draftArticleId?.let { draftArticleCommands.delete(DeleteDraftArticleCommand(it)) }
    }
}
