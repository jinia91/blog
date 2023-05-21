package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException

@UseCaseInteractor
internal class ArticleUseCaseInteractor(
    private val articleIdGenerator: ArticleIdGenerator,
    private val articleRepository: ArticleRepository,
    private val transactionHandler: TransactionHandler,
    private val userServiceClient: UserServiceClient,
) : ArticleCommands {
    override fun post(command: ArticleCommands.PostArticleCommand): ArticleCommands.PostArticleResult = with(command) {
        command.isValid()

        val article = Article.Factory.newPublishedArticle(
            id = articleIdGenerator.generate(),
            writerId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
            tags = tags,
            draftArticleId = draftArticleId,
        )

        transactionHandler.runInReadCommittedTransaction {
            articleRepository.save(article)
        }

        return ArticleCommands.PostArticleResult(articleId = article.id)
    }

    private fun ArticleCommands.PostArticleCommand.isValid() {
        if (!userServiceClient.userExists(writerId)) throw ResourceNotFoundException()
    }
}
