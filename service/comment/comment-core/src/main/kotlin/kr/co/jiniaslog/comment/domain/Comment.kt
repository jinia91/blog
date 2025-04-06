package kr.co.jiniaslog.comment.domain

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

/**
 * 댓글
 *
 * @param id 댓글 식별자
 * @param userInfo 댓글 작성자 정보로 비가입 유저도 존재
 * @param refId 확장성을 고려해 Article만이 아니라 다른 도메인도 참조할 수 있도록 설계
 * @param refType 참조 식별자의 타입
 * @param parent 부모 댓글
 * @param child 자식 댓글
 * @param status 댓글 상태로 삭제된 댓글은 물리적 삭제가 아닌 상태만 변경
 * @param contents 댓글 내용
 */
@Entity
class Comment protected constructor(
    id: CommentId,
    userInfo: UserInfo,
    refId: ReferenceId,
    refType: RefType,
    parent: Comment?,
    child: MutableList<Comment> = mutableListOf(),
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
        AttributeOverride(name = "userId", column = Column(name = "user_id", nullable = true)),
        AttributeOverride(name = "userName", column = Column(name = "user_name", nullable = false)),
        AttributeOverride(name = "password", column = Column(name = "password", nullable = true))
    )
    val userInfo: UserInfo = userInfo

    @AttributeOverride(name = "value", column = Column(name = "ref_id"))
    val refId: ReferenceId = refId

    @Column(name = "ref_type")
    @Enumerated(EnumType.STRING)
    val refType: RefType = refType

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: Status = status
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parents_id")
    val parents: Comment? = null

    @OneToMany(mappedBy = "parents", cascade = [CascadeType.REMOVE])
    @OnDelete(action = OnDeleteAction.CASCADE)
    var child: MutableList<Comment> = mutableListOf()

    @AttributeOverride(name = "value", column = Column(name = "contents"))
    var contents: CommentContents = contents
        private set

    fun addChildComment(
        userInfo: UserInfo,
        refId: ReferenceId,
        refType: RefType,
        contents: CommentContents
    ) {
        val childComment = Comment(
            id = CommentId(IdUtils.generate()),
            userInfo = userInfo,
            refId = refId,
            refType = refType,
            parent = this,
            status = Status.ACTIVE,
            contents = contents
        )
        child.add(childComment)
    }

    companion object {
        fun newOne(
            userInfo: UserInfo,
            refId: ReferenceId,
            refType: RefType,
            parent: Comment? = null,
            contents: CommentContents,
        ): Comment {
            return Comment(
                id = CommentId(IdUtils.generate()),
                userInfo = userInfo,
                refId = refId,
                refType = refType,
                parent = parent,
                status = Status.ACTIVE,
                contents = contents
            )
        }
    }
}
