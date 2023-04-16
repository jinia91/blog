package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.tag.TagId
import kr.co.jiniaslog.blogcore.domain.user.UserId

interface ArticleEditUseCase {
    fun edit(command: ArticleEditCommand)
}

data class ArticleEditCommand(
    val userId: UserId,
    val articleId: ArticleId,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val categoryId: CategoryId,
    val tags: Set<TagId>,
)
