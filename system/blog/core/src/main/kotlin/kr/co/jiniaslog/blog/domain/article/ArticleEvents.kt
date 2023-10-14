package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.draft.DraftArticleId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.shared.core.context.RegisteredEvent
import kr.co.jiniaslog.shared.core.domain.DomainEvent
import java.time.LocalDateTime

@RegisteredEvent
data class PublishedArticleCreatedEvent(
    val articleId: ArticleId,
    val userId: UserId,
    val draftArticleId: DraftArticleId?,
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
) : DomainEvent(CURRENT_VERSION, false) {
    companion object {
        private const val CURRENT_VERSION = 1
    }
}

@RegisteredEvent
data class PublishedArticleDeletedEvent(
    val articleId: ArticleId,
    val userId: UserId,
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
) : DomainEvent(CURRENT_VERSION, false) {
    companion object {
        private const val CURRENT_VERSION = 1
    }
}
