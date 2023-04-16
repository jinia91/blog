package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.application.article.infra.TransactionHandler
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.UserId
import kr.co.jiniaslog.blogcore.domain.article.UserServiceClient
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException

interface TempArticlePostUseCase {
    fun post(command: TempArticlePostCommand)
}

data class TempArticlePostCommand(
    val writerId: UserId,
    val title: String?,
    val content: String?,
    val thumbnailUrl: String?,
    val categoryId: CategoryId?,
)

@UseCaseInteractor
internal class TempArticlePostUseCaseInteractor(
    private val transactionHandler: TransactionHandler,
    private val tempArticleRepository: TempArticleRepository,
    private val userServiceClient: UserServiceClient,
) : TempArticlePostUseCase {

    override fun post(command: TempArticlePostCommand) = with(command) {
        this.isValid()

        val newTemp = TempArticle.Factory.from(
            writerId = writerId,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
        )

        transactionHandler.runInReadCommittedTransaction { tempArticleRepository.save(newTemp) }
    }

    private fun TempArticlePostCommand.isValid() {
        if (!userServiceClient.userExists(writerId)) throw ResourceNotFoundException("$this 's $writerId is not found")
    }
}
