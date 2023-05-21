package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands.DeleteArticleCommand
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands.EditArticleCommand
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands.EditArticleResult
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands.PostArticleCommand
import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands.PostArticleResult
import kr.co.jiniaslog.blogcore.application.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException

private val log = mu.KotlinLogging.logger {}

@UseCaseInteractor
internal class ArticleUseCaseInteractor(
    private val articleIdGenerator: ArticleIdGenerator,
    private val articleRepository: ArticleRepository,
    private val transactionHandler: TransactionHandler,
    private val userServiceClient: UserServiceClient,
) : ArticleCommands, ArticleQueries {
    override fun post(command: PostArticleCommand): PostArticleResult = with(command) {
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

        return PostArticleResult(articleId = article.id)
    }

    private fun PostArticleCommand.isValid() {
        if (!userServiceClient.userExists(writerId)) throw ResourceNotFoundException()
    }

    override fun edit(command: EditArticleCommand): EditArticleResult = with(command) {
        command.isValid()
        val targetArticle = articleRepository.findById(articleId) ?: throw ResourceNotFoundException()
        targetArticle.edit(
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
            tags = tags,
        )

        transactionHandler.runInReadCommittedTransaction {
            articleRepository.save(targetArticle)
        }

        return@with EditArticleResult(articleId = targetArticle.id)
    }

    private fun EditArticleCommand.isValid() {
        if (!userServiceClient.userExists(writerId)) throw ResourceNotFoundException()
    }

    override fun delete(command: DeleteArticleCommand) = with(command) {
        val targetArticle = articleRepository.findById(articleId) ?: throw ResourceNotFoundException()

        transactionHandler.runInReadCommittedTransaction {
            targetArticle.registerPublishedArticleDeletedEvent()
            articleRepository.delete(targetArticle.id)
        }
    }

    override fun getArticle(articleId: ArticleId): Article? {
        return articleRepository.findById(articleId)
    }
}
