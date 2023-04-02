package kr.co.jiniaslog.article.application.usecase

import kr.co.jiniaslog.article.domain.ArticleId
import kr.co.jiniaslog.article.domain.CategoryId
import kr.co.jiniaslog.article.domain.TagId
import kr.co.jiniaslog.article.domain.UserId

interface ArticleEditUseCase {
    fun editArticle(articleEditCommand: ArticleEditCommand)
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
