package kr.co.jiniaslog.blogcore.domain.draft

import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import java.time.LocalDateTime

class DraftArticle private constructor(
    id: DraftArticleId,
    writerId: UserId,
    title: String?,
    content: String?,
    thumbnailUrl: String?,
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
) : AggregateRoot<DraftArticleId>(createdAt, updatedAt) {

    override val id: DraftArticleId = id

    val writerId: UserId = writerId

    var title: String? = title
        private set

    var content: String? = content
        private set

    var thumbnailUrl: String? = thumbnailUrl
        private set

    fun update(
        title: String?,
        content: String?,
        thumbnailUrl: String?,
    ) {
        this.title = title
        this.content = content
        this.thumbnailUrl = thumbnailUrl
    }

    object Factory {
        fun from(
            id: DraftArticleId,
            writerId: UserId,
            title: String? = null,
            content: String? = null,
            thumbnailUrl: String? = null,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
        ): DraftArticle = DraftArticle(
            id = id,
            writerId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        fun newOne(
            id: DraftArticleId,
            writerId: UserId,
            title: String? = null,
            content: String? = null,
            thumbnailUrl: String? = null,
        ): DraftArticle = DraftArticle(
            id = id,
            writerId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            createdAt = null,
            updatedAt = null,
        )
    }
}
