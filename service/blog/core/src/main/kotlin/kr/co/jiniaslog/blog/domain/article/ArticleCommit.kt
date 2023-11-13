package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.shared.core.domain.DomainEntity
import kr.co.jiniaslog.shared.core.domain.IdManager
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
import java.time.LocalDateTime

class ArticleCommit(
    id: ArticleCommitVersion,
) : DomainEntity<ArticleCommitVersion>() {
    override val id: ArticleCommitVersion = id

    var title: ArticleTitle? = null
        private set
    var delta: ArticleContentDelta = ArticleContentDelta.build("")
        private set
    var thumbnailUrl: ArticleThumbnailUrl? = null
        private set
    var categoryId: CategoryId? = null
        private set

//    var tags: Set<Tag>? = null; private set todo implement tags
    companion object {
        val deltaUtil = DiffMatchPatch()

        fun initCommit(): ArticleCommit {
            return ArticleCommit(
                id = ArticleCommitVersion(IdManager.generate()),
            )
        }

        fun commit(
            title: ArticleTitle?,
            delta: ArticleContentDelta,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryId: CategoryId?,
        ): ArticleCommit {
            return ArticleCommit(
                id = ArticleCommitVersion(IdManager.generate()),
            ).apply {
                this.title = title
                this.delta = delta
                this.thumbnailUrl = thumbnailUrl
                this.categoryId = categoryId
            }
        }

        fun from(
            id: ArticleCommitVersion,
            title: ArticleTitle?,
            content: ArticleContentDelta,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryId: CategoryId?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): ArticleCommit {
            return ArticleCommit(
                id = id,
            ).apply {
                this.title = title
                this.delta = content
                this.thumbnailUrl = thumbnailUrl
                this.categoryId = categoryId
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
