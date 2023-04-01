package kr.co.jiniaslog.article.domain

import kr.co.jiniaslog.lib.context.DomainEntity

@DomainEntity
class Article internal constructor(
    id: ArticleId,
    title: String,
    content: String,
    hit: Long,
//    toc: String,
    thumbnailUrl: String,
    writerId: WriterId,
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

//    var toc: String = toc
//        private set
    var thumbnailUrl: String = thumbnailUrl
        private set
    val writerId: WriterId = writerId
    var categoryId: CategoryId = categoryId
        private set
    var tags: Set<TagId> = tags
        private set
}
