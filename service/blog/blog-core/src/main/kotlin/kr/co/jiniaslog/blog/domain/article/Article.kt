package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
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
 * JPA 엔티티로도 사용되므로 프로퍼티가 장황해질 것을 고려, 기본생성자는 private으로 막고 별도 프로퍼티 선언을 하였다.
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
class Article(
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

    @ElementCollection
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