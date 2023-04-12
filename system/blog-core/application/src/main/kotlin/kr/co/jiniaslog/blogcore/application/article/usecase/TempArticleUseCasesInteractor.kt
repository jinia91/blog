package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.UserServiceClient
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor

@UseCaseInteractor
internal class TempArticleUseCasesInteractor(
    private val transactionHandler: TransactionHandler,
    private val TempArticleRepository: TempArticleRepository,
    private val userServiceClient: UserServiceClient,
) : TempArticleUseCases {

    override fun post(command: TempArticlePostCommand) = with(command) {
        if (command.isInvalid()) throw TempArticlePostCommandInValidException("Invalid ArticlePostCommand : $command")

        val newTemp = TempArticle.Factory.newTempOne(
            userId = userId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
        )

        transactionHandler.runInReadCommittedTransaction { TempArticleRepository.save(newTemp) }
    }

    private fun TempArticlePostCommand.isInvalid(): Boolean =
        !userServiceClient.isAdmin(userId.value)

    override fun delete() {
        transactionHandler.runInReadCommittedTransaction { TempArticleRepository.delete() }
    }

    override fun findOne(): TempArticle? =
        TempArticleRepository.getTemp(ArticleId(TempArticle.TEMP_ARTICLE_STATIC_ID))
}
