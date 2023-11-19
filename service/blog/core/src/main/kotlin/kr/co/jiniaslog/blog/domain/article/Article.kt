package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.writer.WriterId
import kr.co.jiniaslog.message.nexus.event.ArticleCommitted
import kr.co.jiniaslog.message.nexus.event.ArticleCreated
import kr.co.jiniaslog.message.nexus.event.ArticleDeleted
import kr.co.jiniaslog.message.nexus.event.ArticlePublished
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

class Article private constructor(
    id: ArticleId,
    writerId: WriterId,
    articleHistory: MutableList<ArticleCommit>,
    stagingSnapShot: ArticleStagingSnapShot? = null,
    status: ArticleStatus,
    head: ArticleCommitVersion,
) : AggregateRoot<ArticleId>() {
    override val id: ArticleId = id
    val writerId: WriterId = writerId
    var history: MutableList<ArticleCommit> = articleHistory
        private set
    var head: ArticleCommitVersion = head
        private set
    var stagingSnapShot: ArticleStagingSnapShot? = stagingSnapShot
        private set

    var status: ArticleStatus = status
        private set

    val latestCommit: ArticleCommit
        get() = history.last()

    val title: ArticleTitle?
        get() = history.firstOrNull { it.id == head }?.title

    val thumbnailUrl: ArticleThumbnailUrl?
        get() = history.firstOrNull { it.id == head }?.thumbnailUrl

    val categoryId: CategoryId?
        get() = history.firstOrNull { it.id == head }?.categoryId

    val content: ArticleContent
        get() = getContentByCommitVer()

    /**
     * 가장 마지막 커밋의 델타를 기준으로 시도한 커밋의 델타를 계산하여 커밋객체를 생성한다.
     *
     * 커밋시 기존의 스테이징 스냅샷은 지운다.
     *
     * 커밋시 head, checkout은 커밋한 커밋 버전으로 변경한다.
     */
    suspend fun commit(
        title: ArticleTitle?,
        newContent: ArticleContent,
        thumbnailUrl: ArticleThumbnailUrl?,
        categoryId: CategoryId?,
    ) {
        val delta = getContentByCommitVer().calculateDelta(newContent)

        ArticleCommit.commit(
            title = title,
            delta = delta,
            thumbnailUrl = thumbnailUrl,
            categoryId = categoryId,
        ).also {
            this.history.add(it)
            this.head = it.id
            this.stagingSnapShot = null
            this.registerEvent(
                ArticleCommitted(
                    articleId = this.id.value,
                    articleCommitId = it.id.value,
                    writerId = this.writerId.value,
                    categoryId = categoryId?.value,
                    head = this.head.value,
                ),
            )
        }
    }

    /**
     * 특정 커밋시점으로 컨텐츠를 재현한다
     */
    fun getContentByCommitVer(commitId: ArticleCommitVersion = history.last().id): ArticleContent {
        return history.fold(ArticleContent("")) { currentContent, commit ->
            if (commit.id <= commitId) {
                return@fold currentContent.apply(commit.delta)
            }
            return@fold currentContent
        }
    }

    /**
     * 임시 저장할 데이터를 스냅샷으로 저장한다
     */
    fun staging(
        title: ArticleTitle?,
        content: ArticleContent,
        thumbnailUrl: ArticleThumbnailUrl?,
        categoryId: CategoryId?,
    ) {
        this.stagingSnapShot =
            ArticleStagingSnapShot.capture(
                id = StagingSnapShotId(IdUtils.generate()),
                articleId = this.id,
                title = title,
                content = content,
                thumbnailUrl = thumbnailUrl,
                categoryId = categoryId,
            )
    }

    /**
     * Delete Event를 발행한다
     */
    suspend fun delete() {
        this.registerEvent(ArticleDeleted(articleId = this.id.value))
    }

    suspend fun publish(checkoutVersion: ArticleCommitVersion) {
        require(title != null) { "article title must not be null at publishing" }
        require(thumbnailUrl != null) { "article thumbnailUrl must not be null at publishing" }
        require(categoryId != null) { "article categoryId must not be null at publishing" }
        require(content.value.isNotBlank()) { "article content must not be blank at publishing" }
        this.head = checkoutVersion
        this.status = ArticleStatus.PUBLISHED
        this.registerEvent(
            ArticlePublished(
                articleId = this.id.value,
                articleCommitId = this.head.value,
                writerId = this.writerId.value,
                categoryId = this.categoryId!!.value,
                head = this.head.value,
            ),
        )
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
                id = id,
                writerId = writerId,
                status = ArticleStatus.DRAFT,
            ).also { // validate at init
                check(it.history.isNotEmpty()) { "article history must not be empty" }
                check(it.history.first().id == it.head) { "article head must be first commit at init" }
            }.also {
                it.registerEvent(ArticleCreated(articleId = it.id.value, articleCommitId = it.head.value))
            }
        }

        fun from(
            id: ArticleId,
            writerId: WriterId,
            articleHistory: MutableList<ArticleCommit>,
            head: ArticleCommitVersion,
            stagingSnapShot: ArticleStagingSnapShot?,
            status: ArticleStatus,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Article {
            return Article(
                id = id,
                writerId = writerId,
                articleHistory = articleHistory,
                head = head,
                stagingSnapShot = stagingSnapShot,
                status = status,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }.also {
                check(it.history.isNotEmpty()) { "article history must not be empty" }
            }
        }
    }
}
