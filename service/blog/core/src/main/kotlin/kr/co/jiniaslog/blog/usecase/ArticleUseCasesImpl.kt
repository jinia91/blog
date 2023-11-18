package kr.co.jiniaslog.blog.usecase

import kotlinx.coroutines.currentCoroutineContext
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.blog.usecase.ArticleCommitCommandUseCase.ArticleCommitCommand
import kr.co.jiniaslog.blog.usecase.ArticleCommitCommandUseCase.CommitInfo
import kr.co.jiniaslog.blog.usecase.ArticleInitCommandUseCase.ArticleInitCommand
import kr.co.jiniaslog.blog.usecase.ArticleInitCommandUseCase.InitialInfo
import kr.co.jiniaslog.blog.usecase.ArticleStagingCommandUseCase.ArticleStagingCommand
import kr.co.jiniaslog.blog.usecase.ArticleStagingCommandUseCase.StagingInfo
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.FetchMode
import kr.co.jiniaslog.shared.core.domain.TransactionHandler

private val log = mu.KotlinLogging.logger {}

@UseCaseInteractor
internal class ArticleUseCasesImpl(
    private val articleRepository: ArticleRepository,
    private val transactionHandler: TransactionHandler,
) : ArticleUseCases {
    override suspend fun init(command: ArticleInitCommand): InitialInfo =
        with(command) {
            validate(command)

            val article =
                Article.init(
                    id = articleRepository.nextId(),
                    writerId = writerId,
                )

            transactionHandler.runInRepeatableReadTransaction {
                articleRepository.save(article)
            }

            val context = currentCoroutineContext()
            log.info {
                """"
                |$context
                """.trimMargin()
            }

            log.info { "article : $article" }

            return InitialInfo(
                articleId = article.id,
            )
        }

    private fun validate(command: ArticleInitCommand) {
        // todo
    }

    override suspend fun staging(command: ArticleStagingCommand): StagingInfo =
        with(command) {
            validate(command)

            val article =
                articleRepository.findById(command.articleId, mode = FetchMode.NONE)
                    ?: throw IllegalArgumentException("article not found")

//            article.staging(
//                title = title,
//                content = content,
//                thumbnailUrl = thumbnailUrl,
//                categoryId = categoryId,
//            )

            transactionHandler.runInRepeatableReadTransaction {
                articleRepository.save(article)
            }

            return StagingInfo(
                articleId = article.id,
            )
        }

    private fun validate(command: ArticleStagingCommand) {
        // todo
    }

    override suspend fun commit(command: ArticleCommitCommand): CommitInfo =
        with(command) {
            validate(command)

            val article =
                articleRepository.findById(command.articleId, mode = FetchMode.ALL)
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
}
