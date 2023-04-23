package kr.co.jiniaslog.blogcore.application.draft.usecase

import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import java.time.LocalDateTime

interface DraftArticleCommands {
    fun upsertDraftArticle(command: UpsertDraftArticleCommand): UpsertDraftArticleResult
    fun deleteDraftArticle(command: DeleteDraftArticleCommand)

    data class UpsertDraftArticleCommand(
        val writerId: Long,
        val draftArticleId: DraftArticleId?,
        val title: String?,
        val content: String?,
        val thumbnailUrl: String?,
        val tags: Set<TagId>,
        val createdAt: LocalDateTime?,
    )

    data class UpsertDraftArticleResult(
        val draftArticleId: Long,
    )

    data class DeleteDraftArticleCommand(
        val draftArticleId: DraftArticleId,
    )
}
