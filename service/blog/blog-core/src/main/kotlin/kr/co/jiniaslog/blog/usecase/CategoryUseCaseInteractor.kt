package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.outbound.persistence.BlogTransactionHandler
import kr.co.jiniaslog.blog.outbound.persistence.CategoryRepository
import kr.co.jiniaslog.blog.usecase.category.ISyncCategories
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class CategoryUseCaseInteractor(
    private val categoryRepository: CategoryRepository,
    private val transactionHandler: BlogTransactionHandler
) : ISyncCategories {
    override fun handle(command: ISyncCategories.Command): ISyncCategories.Info {
        TODO("Not yet implemented")
    }
}
