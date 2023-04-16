package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.blogcore.domain.user.UserServiceClient
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kr.co.jiniaslog.shared.core.domain.ValidationException

interface PublishedArticlePostUseCase {
    fun post(command: PublishedArticlePostCommand): ArticleId
}

data class PublishedArticlePostCommand(
    val writerId: UserId,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val categoryId: CategoryId,
    val tags: Set<TagId>,
)

@UseCaseInteractor
internal class PublishedArticlePostUseCaseInteractor(
    private val transactionHandler: TransactionHandler,
    private val articleIdGenerator: ArticleIdGenerator,
    private val articleRepository: ArticleRepository,
    private val userServiceClient: UserServiceClient,
) : PublishedArticlePostUseCase {

    override fun post(command: PublishedArticlePostCommand) = with(command) {
        this.isValid()

        val article = Article.Factory.newPublishedArticle(
            id = articleIdGenerator.generate(),
            userId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
            tags = tags,
        )

        transactionHandler.runInReadCommittedTransaction { articleRepository.save(article) }
        return@with article.id
    }

    private fun PublishedArticlePostCommand.isValid() {
        if (!userServiceClient.userExists(writerId)) throw ResourceNotFoundException("$this 's $writerId is not found")
        if (tags.isEmpty()) throw ValidationException("$this 's $tags is empty")
        // todo more validation
    }
}
