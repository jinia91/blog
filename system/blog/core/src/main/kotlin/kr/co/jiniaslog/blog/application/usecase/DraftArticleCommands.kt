package kr.co.jiniaslog.blog.application.usecase

import kr.co.jiniaslog.blog.domain.draft.DraftArticleId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.Command

interface DraftArticleCommands {
    fun create(command: CreateDraftArticleCommand): CreateDraftArticleResult
    fun update(command: UpdateDraftArticleCommand): UpdateDraftArticleResult
    fun delete(command: DeleteDraftArticleCommand)

    data class CreateDraftArticleCommand(
        val writerId: UserId,
        val title: String?,
        val content: String?,
        val thumbnailUrl: String?,
    ) : Command(isRecovery = false)

    data class CreateDraftArticleResult(
        val draftArticleId: DraftArticleId,
    )

    data class UpdateDraftArticleCommand(
        val writerId: UserId,
        val draftArticleId: DraftArticleId,
        val title: String?,
        val content: String?,
        val thumbnailUrl: String?,
    ) : Command(isRecovery = false)

    data class UpdateDraftArticleResult(
        val draftArticleId: DraftArticleId,
    )

    data class DeleteDraftArticleCommand(
        val draftArticleId: DraftArticleId,
    ) : Command(isRecovery = false)
}
