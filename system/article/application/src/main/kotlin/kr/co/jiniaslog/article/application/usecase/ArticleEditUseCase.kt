package kr.co.jiniaslog.article.application.usecase

import kr.co.jiniaslog.article.adapter.http.domain.ArticleId
import kr.co.jiniaslog.article.adapter.http.domain.CategoryId
import kr.co.jiniaslog.article.adapter.http.domain.TagId

interface ArticleEditUseCase {
    fun editArticle(articleEditCommand: ArticleEditCommand)
}

data class ArticleEditCommand(
    val articleId: ArticleId,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val categoryId: CategoryId,
    val tags: Set<TagId>,
)
