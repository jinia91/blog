package kr.co.jiniaslog.blogcore.application.draft.usecase

import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.CreateDraftArticleCommand
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.CreateDraftArticleResult
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.DeleteDraftArticleCommand
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.UpdateDraftArticleCommand
import kr.co.jiniaslog.blogcore.application.draft.usecase.DraftArticleCommands.UpdateDraftArticleResult
import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException

@UseCaseInteractor
internal class DraftArticleUseCaseInteractor(
    private val draftArticleIdGenerator: DraftArticleIdGenerator,
    private val draftArticleRepository: DraftArticleRepository,
    private val transactionHandler: TransactionHandler,
    private val userServiceClient: UserServiceClient,
) : DraftArticleCommands, DraftArticleQueries {

    override fun createDraftArticle(command: CreateDraftArticleCommand): CreateDraftArticleResult = with(command) {
        command.isValid()

        val draftArticle = DraftArticle.Factory.newOne(
            id = draftArticleIdGenerator.generate(),
            writerId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
        )

        transactionHandler.runInReadCommittedTransaction {
            draftArticleRepository.save(draftArticle)
        }

        return CreateDraftArticleResult(draftArticleId = draftArticle.id.value)
    }

    private fun CreateDraftArticleCommand.isValid() {
        if (!userServiceClient.userExists(writerId)) throw ResourceNotFoundException()
    }

    override fun updateDraftArticle(command: UpdateDraftArticleCommand): UpdateDraftArticleResult = with(command) {
        command.isValid()

        val draftArticle = draftArticleRepository.getById(draftArticleId) ?: throw ResourceNotFoundException()

        draftArticle.update(
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
        )

        transactionHandler.runInReadCommittedTransaction {
            draftArticleRepository.save(draftArticle)
        }

        return UpdateDraftArticleResult(draftArticleId = draftArticle.id.value)
    }

    private fun UpdateDraftArticleCommand.isValid() {
        if (!userServiceClient.userExists(writerId)) throw ResourceNotFoundException()
    }

    override fun deleteDraftArticle(command: DeleteDraftArticleCommand) {
        transactionHandler.runInReadCommittedTransaction {
            draftArticleRepository.deleteById(command.draftArticleId)
        }
    }

    override fun getDraftArticle(draftArticleId: DraftArticleId): DraftArticle =
        draftArticleRepository.getById(draftArticleId) ?: throw ResourceNotFoundException()
}
