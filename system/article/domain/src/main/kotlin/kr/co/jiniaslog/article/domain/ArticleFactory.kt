package kr.co.jiniaslog.article.domain

import kr.co.jiniaslog.lib.context.DomainService

@DomainService
class ArticleFactory {
    fun newOne(
        id: ArticleId,
        writerId: WriterId,
        title: String,
        content: String,
        thumbnailUrl: String,
        categoryId: CategoryId,
        tags: Set<TagId>,
    ): Article = Article(
        id = id,
        hit = 0,
        writerId = writerId,
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        categoryId = categoryId,
        tags = tags,
    )
}
