package kr.co.jiniaslog.infra

import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

private val log = mu.KotlinLogging.logger { }

@Controller
class BlogCoreArticleEventHandler {
    @ServiceActivator(inputChannel = "ArticleCreatedChannel")
    fun articleCreatedEventHandler(event: ArticleCreated) =
        with(event) {
            log.info { "listen Event : $this" }
        }
}
