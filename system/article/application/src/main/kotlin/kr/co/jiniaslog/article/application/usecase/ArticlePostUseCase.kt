package kr.co.jiniaslog.article.application.usecase

import kr.co.jiniaslog.article.domain.CategoryId
import kr.co.jiniaslog.article.domain.TagId
import kr.co.jiniaslog.article.domain.UserId

interface ArticlePostUseCase {
    fun postArticle(articleCreateCommand: ArticlePostCommand): Long
}

data class ArticlePostCommand(
    val userId: UserId,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val categoryId: CategoryId,
    val tags: Set<TagId>,
)
