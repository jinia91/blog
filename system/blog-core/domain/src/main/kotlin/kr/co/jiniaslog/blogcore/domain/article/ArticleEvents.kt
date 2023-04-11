package kr.co.jiniaslog.blogcore.domain.article

import kr.co.jiniaslog.shared.core.domain.DomainEvent
import java.time.LocalDateTime

data class ArticlePublishedEvent(
    val articleId: ArticleId,
    val userId: UserId,
    private val occurredAt: LocalDateTime = LocalDateTime.now(),
) : DomainEvent(CURRENT_VERSION, occurredAt, false) {
    companion object {
        private const val CURRENT_VERSION = 1
    }
}

data class ArticleEditedEvent(
    val articleId: ArticleId,
    val userId: UserId,
    private val occurredAt: LocalDateTime = LocalDateTime.now(),
) : DomainEvent(CURRENT_VERSION, occurredAt, false) {
    companion object {
        private const val CURRENT_VERSION = 1
    }
}

data class ArticleDraftEvent(
    val articleId: ArticleId,
    val userId: UserId,
    private val occurredAt: LocalDateTime = LocalDateTime.now(),
) : DomainEvent(CURRENT_VERSION, occurredAt, false) {
    companion object {
        private const val CURRENT_VERSION = 1
    }
}
