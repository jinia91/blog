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
    private val tempArticleRepository: TempArticleRepository,
    private val userServiceClient: UserServiceClient,
) : TempArticleUseCases {

    override fun post(command: TempArticlePostCommand) = with(command) {
        if (!command.isValid()) throw TempArticlePostCommandInValidException("Invalid ArticlePostCommand : $command")

        val newTemp = TempArticle.Factory.from(
            writerId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
        )

        transactionHandler.runInReadCommittedTransaction { tempArticleRepository.save(newTemp) }
    }

    private fun TempArticlePostCommand.isValid(): Boolean =
        userServiceClient.userExists(writerId)
    // TODO: category 존재 검증

    override fun delete() {
        transactionHandler.runInReadCommittedTransaction { tempArticleRepository.delete() }
    }

    override fun findOne(): TempArticle? =
        tempArticleRepository.getTemp(ArticleId(TempArticle.TEMP_ARTICLE_STATIC_ID))
}
