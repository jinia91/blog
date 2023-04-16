package kr.co.jiniaslog.blogcore.adapter.messaging

import kr.co.jiniaslog.blogcore.application.article.usecase.TempArticleDeleteUseCase
import kr.co.jiniaslog.blogcore.domain.article.ArticleCreatedEvent
import mu.KotlinLogging
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

private val log = KotlinLogging.logger { }

@Controller
class ArticleEventHandler(
    private val tempArticleDeleteUseCase: TempArticleDeleteUseCase,
) {
    @ServiceActivator(inputChannel = "ArticleCreatedEventChannel")
    fun articleCreatedEventHandler(articleCreatedEvent: ArticleCreatedEvent) {
        log.info { "listen Event : $articleCreatedEvent" }
        tempArticleDeleteUseCase.delete()
    }
}
