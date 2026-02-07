package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.DomainEvent

data class MemoCreatedEvent(
    val memoId: Long,
    val authorId: Long,
    val title: String,
    val content: String,
) : DomainEvent(eventVersion = 1)

data class MemoUpdatedEvent(
    val memoId: Long,
    val authorId: Long,
    val title: String,
    val content: String,
) : DomainEvent(eventVersion = 1)

data class MemoDeletedEvent(
    val memoId: Long,
) : DomainEvent(eventVersion = 1)
