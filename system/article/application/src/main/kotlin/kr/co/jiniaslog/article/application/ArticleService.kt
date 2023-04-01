package kr.co.jiniaslog.article.application

import kr.co.jiniaslog.article.application.infra.TransactionHandler
import kr.co.jiniaslog.article.application.port.ArticleIdGenerator
import kr.co.jiniaslog.article.application.port.ArticleStore
import kr.co.jiniaslog.article.application.usecase.ArticlePostCommand
import kr.co.jiniaslog.article.application.usecase.ArticlePostUseCase
import kr.co.jiniaslog.article.domain.ArticleFactory
import kr.co.jiniaslog.lib.context.UseCaseInteractor

@UseCaseInteractor
internal class ArticleService(
    private val articleStore: ArticleStore,
    private val articleIdGenerator: ArticleIdGenerator,
    private val transactionHandler: TransactionHandler,
    private val articleFactory: ArticleFactory,
) : ArticlePostUseCase {

    override fun postArticle(articleCreateCommand: ArticlePostCommand): Long = with(articleCreateCommand) {
        if (this.isInvalid()) throw IllegalArgumentException("Invalid ArticlePostCommand")
        val article = transactionHandler.runInReadCommittedTransaction {
            articleFactory.newOne(
                id = articleIdGenerator.generate(),
                writerId = writerId,
                title = title,
                content = content,
                thumbnailUrl = thumbnailUrl,
                categoryId = categoryId,
                tags = tags,
            ).also {
                articleStore.save(it)
            }
        }
        return@with article.id.value
    }

    // todo: implement
    private fun ArticlePostCommand.isInvalid(): Boolean = false
}
