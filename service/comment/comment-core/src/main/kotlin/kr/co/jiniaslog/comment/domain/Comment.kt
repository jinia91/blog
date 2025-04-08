package kr.co.jiniaslog.comment.domain

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate
import kr.co.jiniaslog.shared.core.domain.IdUtils

/**
 * 댓글
 *
 * @param id 댓글 식별자
 * @param authorInfo 댓글 작성자 정보로 비가입 유저도 존재
 * @param refId 확장성을 고려해 Article만이 아니라 다른 도메인도 참조할 수 있도록 설계
 * @param refType 참조 식별자의 타입
 * @param parentId 부모 댓글 식별자
 * @param status 댓글 상태로 삭제된 댓글은 물리적 삭제가 아닌 상태만 변경
 * @param contents 댓글 내용
 */
@Entity
class Comment protected constructor(
    id: CommentId,
    authorInfo: AuthorInfo,
    refId: ReferenceId,
    refType: RefType,
    parentId: CommentId?,
    status: Status,
    contents: CommentContents,
) : JpaAggregate<CommentId>() {

    enum class RefType {
        ARTICLE
    }

    enum class Status {
        ACTIVE,
        DELETED
    }

    @EmbeddedId
    @AttributeOverride(name = "value", column = Column(name = "id"))
    override val entityId: CommentId = id

    @AttributeOverrides(
        AttributeOverride(name = "authorId", column = Column(name = "author_id", nullable = true)),
        AttributeOverride(name = "authorName", column = Column(name = "author_name", nullable = false)),
        AttributeOverride(name = "password", column = Column(name = "password", nullable = true)),
        AttributeOverride(name = "profileImageUrl", column = Column(name = "profile_image_url", nullable = true))
    )
    val authorInfo: AuthorInfo = authorInfo

    @AttributeOverride(name = "value", column = Column(name = "ref_id"))
    val refId: ReferenceId = refId

    @Column(name = "ref_type")
    @Enumerated(EnumType.STRING)
    val refType: RefType = refType

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: Status = status
        private set

    @JoinColumn(name = "parents_id")
    @AttributeOverride(name = "value", column = Column(name = "parent_id"))
    val parentId: CommentId? = parentId

    @AttributeOverride(name = "value", column = Column(name = "contents"))
    var contents: CommentContents = contents
        private set

    companion object {
        fun newOne(
            userInfo: AuthorInfo,
            refId: ReferenceId,
            refType: RefType,
            parentId: CommentId? = null,
            contents: CommentContents,
        ): Comment {
            return Comment(
                id = CommentId(IdUtils.generate()),
                authorInfo = userInfo,
                refId = refId,
                refType = refType,
                parentId = parentId,
                status = Status.ACTIVE,
                contents = contents
            )
        }
    }
}
