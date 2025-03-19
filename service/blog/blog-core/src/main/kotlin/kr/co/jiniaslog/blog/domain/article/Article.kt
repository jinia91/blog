package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import kr.co.jiniaslog.blog.domain.MemoId
import kr.co.jiniaslog.blog.domain.UserId
import kr.co.jiniaslog.blog.domain.article.QTagging.tagging
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

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
    hit: Int,
) : JpaAggregate<ArticleId>() {

    /**
     * 게시글 상태
     * - DRAFT: 작성중 - 작성시 바로 생성
     * - PUBLISHED: 게시 - 작성중인 게시글을 게시
     * - DELETED: 삭제 - 게시글 삭제는 물리적 삭제가 아닌 상태만 변경
     */
    enum class Status { DRAFT, PUBLISHED, DELETED }

    @EmbeddedId
    @AttributeOverride(column = Column(name = "id"), name = "value")
    override val entityId: ArticleId = id

    @AttributeOverride(column = Column(name = "author_id"), name = "value")
    val authorId: UserId = authorId

    @Column(name = "status")
    var status: Status = status
        private set

    @AttributeOverride(column = Column(name = "memo_ref_id", nullable = true), name = "value")
    var memoRefId: MemoId? = memoRefId
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

    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = [CascadeType.REMOVE])
    private var _tags: MutableSet<Tagging> = tags

    val tagsId: Set<TagId>
        get() = _tags.map { it.tag.entityId }.toSet()

    val tagsInfo: Map<TagId, String>
        get() = _tags.associate { it.tag.entityId to it.tag.tagName.value }

    /**
     *  초기화시 상태에 따라 게시글의 불변성(Invariant) 검증
     */
    init {
        require(hit >= 0) { "조회수는 양수거나 0이여야 합니다" }
        when (status) {
            Status.DRAFT -> {}

            Status.PUBLISHED -> {
                articleContents.validateOnPublish()
            }

            Status.DELETED -> {
                check(tagsId.isEmpty()) { "삭제된 게시글은 태그를 가질 수 없습니다." }
            }
        }
    }

    /**
     * 게시글을 공개할 수 있다
     *
     * - 게시글의 공개 상태 검증
     * - can / execute pattern
     *
     * @see publish
     */
    val canPublish: Boolean
        get() {
            return status != Status.PUBLISHED &&
                articleContents.canPublish
        }

    val isPublished: Boolean
        get() = status == Status.PUBLISHED

    /**
     * 게시글을 공개한다
     *
     * @see canPublish
     *
     */
    fun publish() {
        check(canPublish) { "게시글을 게시할 수 없습니다." }
        status = Status.PUBLISHED
    }

    /**
     * 게시글을 삭제한다
     *
     * - 삭제된 게시글 롤백을 위해 논리 삭제로 구현
     * - 검색 쿼리 편의를 위해 연관관계는 모두 해제한다
     *
     * @see unDelete
     */
    fun delete() {
        check(status != Status.DELETED) { "이미 삭제된 게시글입니다." }
        _tags.clear()
        memoRefId = null
        status = Status.DELETED
    }

    /**
     * 게시글을 삭제 취소한다
     *
     * @see delete
     */
    fun unDelete() {
        check(status == Status.DELETED) { "이미 삭제되지 않은 게시글입니다." }
        status = Status.DRAFT
    }

    /**
     * 게시글의 내용들을 수정한다
     *
     * - 제목, 썸네일, 내용을 한번에 수정
     *
     * @param articleContents
     */
    fun updateArticleContents(articleContents: ArticleContents) {
        validateContentEditable(articleContents)
        this.articleContents = articleContents
    }

    private fun validateContentEditable(articleContents: ArticleContents) {
        when (status) {
            Status.DRAFT -> {}

            Status.PUBLISHED -> {
                articleContents.validateOnPublish()
            }

            Status.DELETED -> throw IllegalStateException("삭제된 게시글은 수정할 수 없습니다.")
        }
    }

    fun addTag(tag: Tag) {
        check(status != Status.DELETED) { "삭제된 게시글은 태그를 추가할 수 없습니다." }
        val tagging = Tagging(TaggingId(IdUtils.idGenerator.generate()), this, tag)
        _tags.add(tagging)
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
                hit = 0,
            )
        }
    }
}
