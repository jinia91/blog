package kr.co.jiniaslog.blog.adapter.inbound.message

import kotlinx.coroutines.delay
import kr.co.jiniaslog.blog.domain.article.ArticleCommitVersion
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import kr.co.jiniaslog.message.nexus.event.ArticleCommittedEventHandleable
import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import kr.co.jiniaslog.message.nexus.event.ArticleCreatedEventHandleable
import kr.co.jiniaslog.shared.core.domain.FetchMode
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Controller

private val log = mu.KotlinLogging.logger { }

@Controller
class ArticleEventsHandler(
    private val articleRepository: ArticleRepository
) :
    ArticleCreatedEventHandleable,
    ArticleCommittedEventHandleable{
    @ServiceActivator(inputChannel = ArticleCreatedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCreated) {
        delay(1000)
        log.info { "listen $event" }
    }

    @ServiceActivator(inputChannel = ArticleCommittedEventHandleable.CHANNEL_NAME)
    override suspend fun handle(event: ArticleCommitted) {
        log.info { "listen $event" }
        val article = articleRepository.findById(ArticleId(event.articleId), FetchMode.ALL)!!
        val latestArticle = article.getContentAtCommitVer(commitId = ArticleCommitVersion(event.articleCommitId))
        log.info { latestArticle }
    }
}