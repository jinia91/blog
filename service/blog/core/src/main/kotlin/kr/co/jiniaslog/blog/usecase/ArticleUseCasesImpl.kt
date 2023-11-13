package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.TransactionHandler

@UseCaseInteractor
class ArticleUseCasesImpl(
    private val articleRepository: ArticleRepository,
    private val transactionHandler: TransactionHandler,
) : ArticleUseCases {
    override suspend fun init(command: ArticleInitCommand): InitialInfo =
        with(command) {
            validate(command)

            val article =
                Article.init(
                    id = articleRepository.nextId(),
                    writerId = command.writerId,
                )

            transactionHandler.runInRepeatableReadTransaction {
                articleRepository.save(article)
            }

            return InitialInfo(
                articleId = article.id,
            )
        }

    private fun validate(command: ArticleInitCommand) {
        // todo
    }
}
