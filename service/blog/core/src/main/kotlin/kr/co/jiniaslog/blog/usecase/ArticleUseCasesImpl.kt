package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.blog.usecase.ArticleCommitCommandUseCase.ArticleCommitCommand
import kr.co.jiniaslog.blog.usecase.ArticleCommitCommandUseCase.CommitInfo
import kr.co.jiniaslog.blog.usecase.ArticleDeleteCommandUseCase.ArticleDeleteCommand
import kr.co.jiniaslog.blog.usecase.ArticleDeleteCommandUseCase.DeleteInfo
import kr.co.jiniaslog.blog.usecase.ArticleInitCommandUseCase.ArticleInitCommand
import kr.co.jiniaslog.blog.usecase.ArticleInitCommandUseCase.InitialInfo
import kr.co.jiniaslog.blog.usecase.ArticlePublishCommandUseCase.ArticlePublishCommand
import kr.co.jiniaslog.blog.usecase.ArticlePublishCommandUseCase.PublishInfo
import kr.co.jiniaslog.blog.usecase.ArticleStagingCommandUseCase.ArticleStagingCommand
import kr.co.jiniaslog.blog.usecase.ArticleStagingCommandUseCase.StagingInfo
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.TransactionHandler

private val log = mu.KotlinLogging.logger {}

@UseCaseInteractor
internal class ArticleUseCasesImpl(
    private val articleRepository: ArticleRepository,
    private val transactionHandler: TransactionHandler,
) : ArticleUseCases {
    override suspend fun init(command: ArticleInitCommand): InitialInfo =
        with(command) {
            log.debug { "init article command: $command" }
            validate(command)

            val article =
                Article.init(
                    id = articleRepository.nextId(),
                    writerId = writerId,
                )

            transactionHandler.runInRepeatableReadTransaction {
                articleRepository.save(article)
            }

            return InitialInfo(
                articleId = article.id,
            ).also {
                log.debug { "init article success: $it" }
            }
        }

    private fun validate(command: ArticleInitCommand) {
        // todo
    }

    override suspend fun staging(command: ArticleStagingCommand): StagingInfo =
        with(command) {
            log.debug { "staging article command: $command" }
            validate(command)

            val article =
                articleRepository.findById(command.articleId)
                    ?: throw IllegalArgumentException("article not found")

            article.staging(
                title = title,
                content = content,
                thumbnailUrl = thumbnailUrl,
                categoryId = categoryId,
            )

            transactionHandler.runInRepeatableReadTransaction {
                articleRepository.save(article)
            }

            return StagingInfo(
                articleId = article.id,
            ).also {
                log.debug { "staging article success: $it" }
            }
        }

    private fun validate(command: ArticleStagingCommand) {
        // todo
    }

    override suspend fun commit(command: ArticleCommitCommand): CommitInfo =
        with(command) {
            validate(command)

            val article =
                articleRepository.findById(command.articleId)
                    ?: throw IllegalArgumentException("article not found")

            article.commit(
                title = title,
                newContent = content,
                thumbnailUrl = thumbnailUrl,
                categoryId = categoryId,
            )

            transactionHandler.runInRepeatableReadTransaction {
                articleRepository.save(article)
            }

            return CommitInfo(
                articleId = article.id,
                commitId = article.latestCommit.id,
            )
        }

    private fun validate(command: ArticleCommitCommand) {
        // todo
    }

    override suspend fun delete(command: ArticleDeleteCommand): DeleteInfo =
        with(command) {
            validate(command)

            val article =
                articleRepository.findById(command.articleId)
                    ?: throw IllegalArgumentException("article not found")

            article.delete()

            transactionHandler.runInRepeatableReadTransaction {
                articleRepository.deleteById(article.id)
            }

            return DeleteInfo(result = true)
        }

    private fun validate(command: ArticleDeleteCommand) {
        // todo
    }

    override suspend fun publish(command: ArticlePublishCommand): PublishInfo =
        with(command) {
            validate(command)

            val article =
                articleRepository.findById(command.articleId)
                    ?: throw IllegalArgumentException("article not found")

            article.publish(headVersion)

            transactionHandler.runInRepeatableReadTransaction {
                articleRepository.save(article)
            }

            return PublishInfo(
                articleId = article.id,
            )
        }

    private fun validate(command: ArticlePublishCommand) {
        // todo
    }
}
