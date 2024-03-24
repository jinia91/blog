package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.memo.MemoId
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils

@Entity
class Article private constructor(
    id: ArticleId,
    memoRefId: MemoId?,
    authorId: UserId,
    articleContents: ArticleContents,
    tags: MutableSet<Tagging>,
    categoryId: CategoryId,
    hit: Int,
    likes: MutableSet<UserLike>,
) : AggregateRoot<ArticleId>() {
    @EmbeddedId
    @AttributeOverride(
        column = Column(name = "article_id"),
        name = "value",
    )
    override val id: ArticleId = id

    @AttributeOverride(
        column = Column(name = "author_id"),
        name = "value",
    )
    val authorId: UserId = authorId

    @AttributeOverride(
        column = Column(name = "memo_ref_id", nullable = true),
        name = "value",
    )
    var memoRefId: MemoId? = memoRefId
        private set

    @AttributeOverride(column = Column(name = "category_id"), name = "value")
    var category: CategoryId = categoryId
        private set

    @AttributeOverrides(
        AttributeOverride(name = "title", column = Column(name = "title")),
        AttributeOverride(name = "contents", column = Column(name = "contents")),
        AttributeOverride(name = "thumbnailUrl", column = Column(name = "thumbnail_url")),
    )
    var articleContents: ArticleContents = articleContents
        private set

    @Column(name = "hit")
    var hit: Int = hit
        private set

    @ElementCollection
    var tags: MutableSet<Tagging> = tags
        private set

    @ElementCollection
    var likes: MutableSet<UserLike> = likes
        private set

    @PrePersist
    @PreUpdate
    fun validate() {
        require(hit >= 0) { "hit must be positive" }
    }

    fun edit(
        memoRefId: MemoId?,
        categoryId: CategoryId,
        articleContents: ArticleContents,
        tags: List<TagId>,
    ) {
        this.memoRefId = memoRefId
        this.category = categoryId
        this.articleContents = articleContents
        this.tags = tags.map { Tagging(it) }.toMutableSet()
    }

    companion object {
        fun newOne(
            memoRefId: MemoId?,
            authorId: UserId,
            categoryId: CategoryId,
            articleContents: ArticleContents,
            tags: List<TagId>,
        ): Article {
            return Article(
                id = ArticleId(IdUtils.idGenerator.generate()),
                memoRefId = memoRefId,
                authorId = authorId,
                articleContents = articleContents,
                tags = tags.map { Tagging(it) }.toMutableSet(),
                categoryId = categoryId,
                hit = 0,
                likes = emptySet<UserLike>().toMutableSet(),
            )
        }
    }
}