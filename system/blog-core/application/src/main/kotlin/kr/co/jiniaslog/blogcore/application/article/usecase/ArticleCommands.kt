package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId

interface ArticleCommands {
    fun post(command: PostArticleCommand): PostArticleResult

    data class PostArticleCommand(
        val writerId: UserId,
        val title: String,
        val content: String,
        val thumbnailUrl: String,
        val categoryId: CategoryId,
        val tags: Set<TagId>,
        val draftArticleId: DraftArticleId?,
    )

    data class PostArticleResult(
        val articleId: ArticleId,
    )
}
