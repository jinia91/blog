package kr.co.jiniaslog.blogcore.adapter.http.article

import kr.co.jiniaslog.blogcore.application.article.usecase.ArticleCommands
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId

data class ArticlePostApiRequest(
    val writerId: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val categoryId: Long,
    val tags: Set<Long>,
    val draftArticleId: Long?,
) {
    fun toCommand(): ArticleCommands.PostArticleCommand {
        return ArticleCommands.PostArticleCommand(
            writerId = UserId(writerId),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            categoryId = CategoryId(categoryId),
            tags = tags.map { TagId(it) }.toSet(),
            draftArticleId = draftArticleId?.let { DraftArticleId(it) },
        )
    }
}

data class ArticlePostApiResponse(
    val articleId: Long,
)
