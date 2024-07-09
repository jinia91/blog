package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.memo.MemoId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils

/**
 * 블로그 게시글
 *
 * 블로그의 메인 비즈니스 로직을 담당하는 Aggregate Root 로 설계되었다.
 *
 * JPA 엔티티로도 사용되므로 프로퍼티가 장황해질 것을 고려해, 생성자와 프로퍼티를 분리하였다.
 *
 * 생성자는 테스트 코드 이외에는 사용하지 않는것을 원칙으로 한다
 *
 * @param id 게시글 식별자
 * @param memoRefId 원본이 되는 메모 식별자
 * @param authorId 작성자 식별자
 * @param articleContents 게시글 상세 내용 VO
 * @param status 게시글 상태
 * @param tags 게시글에 달린 태그 목록
 * @param categoryId 게시글이 속한 카테고리 식별자
 * @param hit 조회수
 */
@Entity
class Article internal constructor(
    id: ArticleId,
    memoRefId: MemoId?,
    authorId: UserId,
    status: Status,
    articleContents: ArticleContents,
    tags: MutableSet<Tagging>,
    categoryId: CategoryId?,
    hit: Int,
) : AggregateRoot<ArticleId>() {

    /**
     * 게시글 상태
     * - DRAFT: 작성중 - 작성시 바로 생성
     * - PUBLISHED: 게시 - 작성중인 게시글을 게시
     * - DELETED: 삭제 - 게시글 삭제는 물리적 삭제가 아닌 상태만 변경
     */
    enum class Status { DRAFT, PUBLISHED, DELETED }

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

    @Column(name = "article_status")
    var status: Status = status
        private set

    @AttributeOverride(
        column = Column(name = "memo_ref_id", nullable = true),
        name = "value",
    )
    var memoRefId: MemoId? = memoRefId
        private set

    @AttributeOverride(column = Column(name = "category_id"), name = "value")
    var categoryId: CategoryId? = categoryId
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

    @ElementCollection(fetch = FetchType.EAGER)
    var tags: MutableSet<Tagging> = tags
        private set

    init {
        id.validate()
        authorId.validate()
        articleContents.validate()
        require(hit >= 0) { "조회수는 양수거나 0이여야 합니다" }
        when (status) {
            Status.DRAFT -> {}
            Status.PUBLISHED -> {
                requireNotNull(categoryId) { "게시글이 속한 카테고리는 필수입니다." }
                categoryId.validate()
                require(articleContents.title.isNotBlank()) { "제목은 필수입니다." }
                require(articleContents.contents.isNotBlank()) { "내용은 필수입니다." }
                require(articleContents.thumbnailUrl.isNotBlank()) { "썸네일은 필수입니다." }
                tags.forEach { it.validate() }
            }
            Status.DELETED -> {
                require(categoryId == null) { "삭제된 게시글은 카테고리를 가질 수 없습니다." }
                require(tags.isEmpty()) { "삭제된 게시글은 태그를 가질 수 없습니다." }
            }
        }
    }

    fun publish() {
        require(canPublish()) { "게시글을 게시할 수 없습니다." }
        status = Status.PUBLISHED
    }

    fun canPublish(): Boolean {
        val isNotPublished = this.status != Status.PUBLISHED
        val hasCategory = this.categoryId != null
        val hasTitle = this.articleContents.title.isNotBlank()
        val hasContents = this.articleContents.contents.isNotBlank()
        val hasThumbnail = this.articleContents.thumbnailUrl.isNotBlank()
        return isNotPublished && hasCategory && hasTitle && hasContents && hasThumbnail
    }

    fun delete() {
        require(status != Status.DELETED) { "이미 삭제된 게시글입니다." }
        categoryId = null
        tags.clear()
        memoRefId = null
        status = Status.DELETED
    }

    fun unDelete() {
        require(status == Status.DELETED) { "이미 삭제되지 않은 게시글입니다." }
        status = Status.DRAFT
    }

    fun editContents(articleContents: ArticleContents) {
        this.articleContents = articleContents
    }

    companion object {
        fun newOne(
            authorId: UserId,
        ): Article {
            return Article(
                id = ArticleId(IdUtils.idGenerator.generate()),
                memoRefId = null,
                authorId = authorId,
                status = Status.DRAFT,
                articleContents = ArticleContents.EMPTY,
                tags = mutableSetOf(),
                categoryId = null,
                hit = 0,
            )
        }
    }
}
