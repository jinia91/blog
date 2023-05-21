package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.Command

interface ArticleCommands {
    fun post(command: PostArticleCommand): PostArticleResult
    fun edit(command: EditArticleCommand): EditArticleResult
    fun delete(command: DeleteArticleCommand)

    data class PostArticleCommand(
        val writerId: UserId,
        val title: String,
        val content: String,
        val thumbnailUrl: String,
        val categoryId: CategoryId,
        val tags: Set<TagId>,
        val draftArticleId: DraftArticleId?,
    ) : Command(false)

    data class PostArticleResult(
        val articleId: ArticleId,
    )

    data class EditArticleCommand(
        val articleId: ArticleId,
        val writerId: UserId,
        val title: String,
        val content: String,
        val thumbnailUrl: String,
        val categoryId: CategoryId,
        val tags: Set<TagId>,
    ) : Command(false)

    data class EditArticleResult(
        val articleId: ArticleId,
    )

    data class DeleteArticleCommand(
        val articleId: ArticleId,
    ) : Command(false)
}
