package kr.co.jiniaslog.article.application.usecase

import kr.co.jiniaslog.article.adapter.http.domain.CategoryId
import kr.co.jiniaslog.article.adapter.http.domain.TagId
import kr.co.jiniaslog.article.adapter.http.domain.WriterId

interface ArticlePostUseCase {
    fun postArticle(articleCreateCommand: ArticlePostCommand): Long
}

data class ArticlePostCommand(
    val writerId: WriterId,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val categoryId: CategoryId,
    val tags: Set<TagId>,
)
