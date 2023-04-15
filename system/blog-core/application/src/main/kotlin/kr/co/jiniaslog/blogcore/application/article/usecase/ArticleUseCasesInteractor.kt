package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.UserServiceClient
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor

@UseCaseInteractor
internal class ArticleUseCasesInteractor(
    private val transactionHandler: TransactionHandler,
    private val articleIdGenerator: ArticleIdGenerator,
    private val articleRepository: ArticleRepository,
    private val userServiceClient: UserServiceClient,
) : DraftArticlePostUseCase {

    override fun post(command: DraftArticlePostCommand) = with(command) {
        if (command.isInvalid()) throw TempArticlePostCommandInValidException("Invalid ArticlePostCommand : $command")

        val article = Article.Factory.newDraftOne(
            id = articleIdGenerator.generate(),
            userId = userId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
            tags = tags,
        )

        transactionHandler.runInReadCommittedTransaction { articleRepository.save(article) }
        return@with article.id
    }

    private fun DraftArticlePostCommand.isInvalid(): Boolean =
        !userServiceClient.userExists(userId)
}
