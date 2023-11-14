package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdManager
import java.time.LocalDateTime

class Article private constructor(
    id: ArticleId,
    writerId: WriterId,
    articleHistory: MutableList<ArticleCommit>,
    head: ArticleCommitVersion,
    checkout: ArticleCommitVersion,
    stagingSnapShot: ArticleStagingSnapShot? = null,
) : AggregateRoot<ArticleId>() {
    override val id: ArticleId = id
    val writerId: WriterId = writerId
    var history: MutableList<ArticleCommit> = articleHistory
        private set
    var head: ArticleCommitVersion = head
        private set
    var checkout: ArticleCommitVersion = checkout
        private set
    var stagingSnapShot: ArticleStagingSnapShot? = stagingSnapShot
        private set
//    var tags: Set<Tag> todo tags

    val latestCommit: ArticleCommit
        get() = history.last()

    /**
     * 작업중인 임시 데이터를 스테이징한다.
     */
    fun staging(
        title: ArticleTitle?,
        content: ArticleContent?,
        thumbnailUrl: ArticleThumbnailUrl?,
        categoryId: CategoryId?,
    ) {
        this.stagingSnapShot =
            ArticleStagingSnapShot
                .capture(
                    id = StagingSnapShotId(IdManager.generate()),
                    articleId = this.id,
                    title = title,
                    content = content,
                    thumbnailUrl = thumbnailUrl,
                    categoryId = categoryId,
                )
    }

    /**
     * 가장 마지막 커밋의 델타를 기준으로 시도한 커밋의 델타를 계산하여 커밋객체를 생성한다.
     *
     * 커밋시 기존의 스테이징 스냅샷은 지운다.
     */
    suspend fun commit(
        title: ArticleTitle?,
        newContent: ArticleContent,
        thumbnailUrl: ArticleThumbnailUrl?,
        categoryId: CategoryId?,
    ) {
        val delta = getContentAtCommitVer().calculateDelta(newContent)

        ArticleCommit.commit(
            title = title,
            delta = delta,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
        ).also {
            this.history.add(it)
            this.head = it.id
            this.checkout = it.id
            clearSnapShot()
            this.registerEvent(ArticleCommitted(articleId = this.id.value, articleCommitId = it.id.value))
        }
    }

    private fun clearSnapShot() {
        this.stagingSnapShot = null
    }

    /**
     * 특정 커밋시점으로 컨텐츠를 재현한다
     */
    fun getContentAtCommitVer(commitId: ArticleCommitVersion = history.last().id): ArticleContent {
        return history.fold(ArticleContent("")) { currentContent, commit ->
            if (commit.id <= commitId) {
                return@fold currentContent.apply(commit)
            }
            return@fold currentContent
        }
    }

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
            stagingSnapShot: ArticleStagingSnapShot?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Article {
            return Article(
                id = id,
                writerId = writerId,
                articleHistory = articleHistory,
                head = head,
                checkout = checkout,
                stagingSnapShot = stagingSnapShot,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
