package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.shared.core.domain.DomainEntity

class ArticleMeta private constructor(
    articleHistory: ArrayList<ArticleCommit>,
    head: ArticleCommit,
    override val id: ArticleId,
    val writerId: WriterId,
) : DomainEntity<ArticleId>() {
    var history: ArrayList<ArticleCommit> = articleHistory; private set
    var head: ArticleCommit = head; private set
    var checkout: ArticleCommit? = null; private set
//    var tags: Set<Tag> todo tags

    companion object Factory{
        fun init(
            id: ArticleId,
            writerId: WriterId,
        ) : ArticleMeta {
            val initialCommit = ArticleCommit.initCommit()
            return ArticleMeta(
                articleHistory = arrayListOf(initialCommit),
                head = initialCommit,
                id = id,
                writerId = writerId,
            )
        }
    }
}