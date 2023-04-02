package kr.co.jiniaslog.article.domain

import kr.co.jiniaslog.lib.context.DomainService

@DomainService
class ArticleFactory {
    fun newOne(
        id: ArticleId,
        userId: UserId,
        title: String,
        content: String,
        thumbnailUrl: String,
        categoryId: CategoryId,
        tags: Set<TagId>,
    ): Article = Article(
        id = id,
        hit = 0,
        userId = userId,
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        categoryId = categoryId,
        tags = tags,
    )
}
