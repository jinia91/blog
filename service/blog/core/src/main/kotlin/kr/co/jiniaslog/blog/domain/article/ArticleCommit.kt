package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.shared.core.domain.DomainEntity
import kr.co.jiniaslog.shared.core.domain.IdManager

class ArticleCommit(
    id: ArticleCommitVersion,
) : DomainEntity<ArticleCommitVersion>() {
    override val id: ArticleCommitVersion = id

    var title: ArticleTitle? = null
        private set
    var content: ArticleContent? = null
        private set
    var thumbnailUrl: ArticleThumbnailUrl? = null
        private set
    var categoryId: CategoryId? = null
        private set
//    var tags: Set<Tag>? = null; private set todo implement tags

    fun updateTitle(title: ArticleTitle) {
        this.title = title
    }

    fun updateContent(content: ArticleContent) {
        this.content = content
    }

    fun updateThumbnailUrl(thumbnailUrl: ArticleThumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl
    }

    companion object {
        fun initCommit(): ArticleCommit {
            return ArticleCommit(
                id = ArticleCommitVersion(IdManager.generate()),
            )
        }

        fun from(
            id: ArticleCommitVersion,
            title: ArticleTitle?,
            content: ArticleContent?,
            thumbnailUrl: ArticleThumbnailUrl?,
            categoryId: CategoryId?,
        ): ArticleCommit {
            return ArticleCommit(
                id = id,
            ).apply {
                this.title = title
                this.content = content
                this.thumbnailUrl = thumbnailUrl
                this.categoryId = categoryId
            }
        }
    }
}
