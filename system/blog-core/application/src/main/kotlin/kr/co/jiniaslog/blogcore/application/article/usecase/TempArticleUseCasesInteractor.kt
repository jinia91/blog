package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.application.article.infra.UserServiceClient
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleService
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor

@UseCaseInteractor
internal class TempArticleUseCasesInteractor(
    private val transactionHandler: TransactionHandler,
    private val tempArticleService: TempArticleService,
    private val userServiceClient: UserServiceClient,
) : TempArticleUseCases {

    override fun post(command: TempArticlePostCommand) = with(command) {
        if (this.isInvalid()) throw IllegalArgumentException("Invalid ArticlePostCommand")

        val newTemp = TempArticle.Factory.newTempOne(
            userId = userId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
            tags = tags,
        )

        transactionHandler.runInReadCommittedTransaction { tempArticleService.saveTempArticle(newTemp) }
    }

    private fun TempArticlePostCommand.isInvalid(): Boolean =
        !userServiceClient.isAdmin(userId.value)

    override fun delete() {
        transactionHandler.runInReadCommittedTransaction { tempArticleService.deleteTempArticle() }
    }

    override fun findOne(): TempArticle? =
        tempArticleService.findTempArticle()
}
