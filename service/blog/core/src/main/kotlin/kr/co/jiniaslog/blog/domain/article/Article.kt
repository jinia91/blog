package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import kr.co.jiniaslog.shared.core.domain.AggregateRoot

class Article private constructor(
    id: ArticleId,
    writerId: WriterId,
    articleHistory: MutableList<ArticleCommit>,
    head: ArticleCommitVersion,
    checkout: ArticleCommitVersion,
) : AggregateRoot<ArticleId>() {
    override val id: ArticleId = id
    val writerId: WriterId = writerId
    var history: MutableList<ArticleCommit> = articleHistory
        private set
    var head: ArticleCommitVersion = head
        private set
    var checkout: ArticleCommitVersion = checkout
        private set
//    var tags: Set<Tag> todo tags

    companion object {
        suspend fun init(
            id: ArticleId,
            writerId: WriterId,
        ): Article {
            val initialCommit = ArticleCommit.initCommit()
            return Article(
                articleHistory = mutableListOf(initialCommit),
                head = initialCommit.id,
                checkout = initialCommit.id,
                id = id,
                writerId = writerId,
            ).also { // validate at init
                check(it.history.isNotEmpty()) { "article history must not be empty" }
                check(it.history.first().id == it.head) { "article head must be first commit at init" }
                check(it.checkout == it.head) { "article checkout must be head at init" }
            }.also {
                it.registerEvent(ArticleCreated(articleId = it.id.value, articleCommitId = it.head.value))
            }
        }

        fun from(
            id: ArticleId,
            writerId: WriterId,
            articleHistory: MutableList<ArticleCommit>,
            head: ArticleCommitVersion,
            checkout: ArticleCommitVersion,
        ): Article {
            return Article(
                id = id,
                writerId = writerId,
                articleHistory = articleHistory,
                head = head,
                checkout = checkout,
            )
        }
    }
}
