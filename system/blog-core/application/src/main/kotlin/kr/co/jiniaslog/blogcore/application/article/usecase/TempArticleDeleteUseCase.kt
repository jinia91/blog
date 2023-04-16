package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor

interface TempArticleDeleteUseCase {
    fun delete()
}

@UseCaseInteractor
internal class TempArticleDeleteUseCaseInteractor(
    private val transactionHandler: TransactionHandler,
    private val tempArticleRepository: TempArticleRepository,
) : TempArticleDeleteUseCase {
    override fun delete() {
        transactionHandler.runInReadCommittedTransaction { tempArticleRepository.delete() }
    }
}
