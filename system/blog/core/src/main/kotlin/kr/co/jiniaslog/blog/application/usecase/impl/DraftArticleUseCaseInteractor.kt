package kr.co.jiniaslog.blog.application.usecase.impl

import kr.co.jiniaslog.blog.application.usecase.DraftArticleCommands.CreateDraftArticleCommand
import kr.co.jiniaslog.blog.application.usecase.DraftArticleCommands.CreateDraftArticleResult
import kr.co.jiniaslog.blog.application.usecase.DraftArticleCommands.DeleteDraftArticleCommand
import kr.co.jiniaslog.blog.application.usecase.DraftArticleCommands.UpdateDraftArticleCommand
import kr.co.jiniaslog.blog.application.usecase.DraftArticleCommands.UpdateDraftArticleResult
import kr.co.jiniaslog.blog.application.infra.TransactionHandler
import kr.co.jiniaslog.blog.application.usecase.DraftArticleCommands
import kr.co.jiniaslog.blog.application.usecase.DraftArticleQueries
import kr.co.jiniaslog.blog.domain.draft.DraftArticle
import kr.co.jiniaslog.blog.domain.draft.DraftArticleId
import kr.co.jiniaslog.blog.domain.draft.DraftArticleIdGenerator
import kr.co.jiniaslog.blog.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blog.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException

@UseCaseInteractor
internal class DraftArticleUseCaseInteractor(
    private val draftArticleIdGenerator: DraftArticleIdGenerator,
    private val draftArticleRepository: DraftArticleRepository,
    private val transactionHandler: TransactionHandler,
    private val userServiceClient: UserServiceClient,
) : DraftArticleCommands, DraftArticleQueries {

    override fun create(command: CreateDraftArticleCommand): CreateDraftArticleResult = with(command) {
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

        return CreateDraftArticleResult(draftArticleId = draftArticle.id)
    }

    private fun CreateDraftArticleCommand.isValid() {
        if (!userServiceClient.doesUserExist(writerId)) throw ResourceNotFoundException()
    }

    override fun update(command: UpdateDraftArticleCommand): UpdateDraftArticleResult = with(command) {
        command.isValid()

        val draftArticle = draftArticleRepository.findById(draftArticleId) ?: throw ResourceNotFoundException()

        draftArticle.update(
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
        )

        transactionHandler.runInReadCommittedTransaction {
            draftArticleRepository.update(draftArticle)
        }

        return UpdateDraftArticleResult(draftArticleId = draftArticle.id)
    }

    private fun UpdateDraftArticleCommand.isValid() {
        if (!userServiceClient.doesUserExist(writerId)) throw ResourceNotFoundException()
    }

    override fun delete(command: DeleteDraftArticleCommand) = with(command) {
        draftArticleRepository.findById(draftArticleId) ?: throw ResourceNotFoundException()
        transactionHandler.runInReadCommittedTransaction {
            draftArticleRepository.deleteById(draftArticleId)
        }
    }

    override fun getDraftArticle(draftArticleId: DraftArticleId): DraftArticle =
        draftArticleRepository.findById(draftArticleId) ?: throw ResourceNotFoundException()
}
