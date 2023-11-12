package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.shared.core.domain.AggregateRoot

class Article private constructor(
    id: ArticleId,
    writerId: WriterId,
    articleHistory: ArrayList<ArticleCommit>,
    head: ArticleCommit,
) : AggregateRoot<ArticleId>() {
    override val id: ArticleId = id
    var writerId: WriterId = writerId
        private set
    var history: ArrayList<ArticleCommit> = articleHistory
        private set
    var head: ArticleCommit = head
        private set
    var checkout: ArticleCommit? = null
        private set
//    var tags: Set<Tag> todo tags

    companion object {
        fun init(
            id: ArticleId,
            writerId: WriterId,
        ): Article {
            val initialCommit = ArticleCommit.initCommit()
            return Article(
                articleHistory = arrayListOf(initialCommit),
                head = initialCommit,
                id = id,
                writerId = writerId,
            )
        }
    }
}
