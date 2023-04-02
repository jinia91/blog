package kr.co.jiniaslog.article.domain

import kr.co.jiniaslog.lib.context.DomainEntity

@DomainEntity
class Article(
    id: ArticleId,
    title: String,
    content: String,
    hit: Long,
    thumbnailUrl: String,
    userId: UserId,
    categoryId: CategoryId,
    tags: Set<TagId>,
) {
    val id: ArticleId = id
    var title: String = title
        private set
    var content: String = content
        private set
    var hit: Long = hit
        private set
    var thumbnailUrl: String = thumbnailUrl
        private set
    val writerId: UserId = userId
    var categoryId: CategoryId = categoryId
        private set
    var tags: Set<TagId> = tags
        private set

    fun edit(title: String, content: String, thumbnailUrl: String, categoryId: CategoryId, tags: Set<TagId>): Article {
        this.title = title
        this.content = content
        this.thumbnailUrl = thumbnailUrl
        this.categoryId = categoryId
        this.tags = tags
        return this
    }
}
