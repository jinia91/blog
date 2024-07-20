package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.category.CategorySyncer
import kr.co.jiniaslog.blog.outbound.persistence.BlogTransactionHandler
import kr.co.jiniaslog.blog.outbound.persistence.CategoryRepository
import kr.co.jiniaslog.blog.usecase.category.ISyncCategories
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class CategoryUseCaseInteractor(
    private val categoryRepository: CategoryRepository,
    private val categorySyncer: CategorySyncer,
    private val transactionHandler: BlogTransactionHandler
) : ISyncCategories {
    override fun handle(command: ISyncCategories.Command): ISyncCategories.Info {
        transactionHandler.runInRepeatableReadTransaction {
            val asIsCategories = categoryRepository.findAll()
            val result = categorySyncer.syncCategories(
                asIs = asIsCategories,
                toBe = command.toBeSyncCategories
            )
            categoryRepository.saveAll(result.toBeInsert)
            categoryRepository.saveAll(result.toBeUpdate)
            categoryRepository.deleteAll(result.toBeDelete)
        }
        return ISyncCategories.Info()
    }
}
