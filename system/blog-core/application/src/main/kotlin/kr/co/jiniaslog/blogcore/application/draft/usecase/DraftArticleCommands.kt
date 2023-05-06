package kr.co.jiniaslog.blogcore.application.draft.usecase

import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.user.UserId

interface DraftArticleCommands {
    fun create(command: CreateDraftArticleCommand): CreateDraftArticleResult
    fun update(command: UpdateDraftArticleCommand): UpdateDraftArticleResult
    fun delete(command: DeleteDraftArticleCommand)

    data class CreateDraftArticleCommand(
        val writerId: UserId,
        val title: String?,
        val content: String?,
        val thumbnailUrl: String?,
    )

    data class CreateDraftArticleResult(
        val draftArticleId: DraftArticleId,
    )

    data class UpdateDraftArticleCommand(
        val writerId: UserId,
        val draftArticleId: DraftArticleId,
        val title: String?,
        val content: String?,
        val thumbnailUrl: String?,
    )

    data class UpdateDraftArticleResult(
        val draftArticleId: DraftArticleId,
    )

    data class DeleteDraftArticleCommand(
        val draftArticleId: DraftArticleId,
    )
}
