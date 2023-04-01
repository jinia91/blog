package kr.co.jiniaslog.article.application

import kr.co.jiniaslog.article.application.infra.TransactionHandler
import kr.co.jiniaslog.article.application.port.ArticleIdGenerator
import kr.co.jiniaslog.article.application.port.ArticleRepository
import kr.co.jiniaslog.article.application.usecase.ArticleEditCommand
import kr.co.jiniaslog.article.application.usecase.ArticleEditUseCase
import kr.co.jiniaslog.article.application.usecase.ArticlePostCommand
import kr.co.jiniaslog.article.application.usecase.ArticlePostUseCase
import kr.co.jiniaslog.article.domain.ArticleFactory
import kr.co.jiniaslog.lib.context.UseCaseInteractor

@UseCaseInteractor
internal class ArticleService(
    private val articleRepository: ArticleRepository,
    private val articleIdGenerator: ArticleIdGenerator,
    private val transactionHandler: TransactionHandler,
    private val articleFactory: ArticleFactory,
) : ArticlePostUseCase, ArticleEditUseCase {

    // todo: cache impl
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
                articleRepository.save(it)
            }
        }
        return@with article.id.value
    }

    /**
     *     todo: implement
     *     - writerId, categoryId, tagId가 존재하는지 확인
     */
    private fun ArticlePostCommand.isInvalid(): Boolean = false

    override fun editArticle(articleEditCommand: ArticleEditCommand) = with(articleEditCommand) {
        if (this.isInvalid()) throw IllegalArgumentException("Invalid ArticleEditCommand")
        articleRepository.findById(articleId)
            ?.edit(
                title = title,
                content = content,
                thumbnailUrl = thumbnailUrl,
                categoryId = categoryId,
                tags = tags,
            )?.let {
                transactionHandler.runInReadCommittedTransaction { articleRepository.save(it) }
            } ?: throw IllegalArgumentException("Article not found")
    }

    /**
     *     todo: implement
     *     - writerId, categoryId, tagId가 존재하는지 확인
     */
    private fun ArticleEditCommand.isInvalid(): Boolean = false
}
