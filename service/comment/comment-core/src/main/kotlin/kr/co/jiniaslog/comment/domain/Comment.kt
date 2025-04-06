package kr.co.jiniaslog.comment.domain

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate

@Entity
class Comment protected constructor(
    id: CommentId,
    userInfo: UserInfo,
    refId: ReferenceId,
    status: Status,
    contents: CommentContents
) : JpaAggregate<CommentId>() {

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
        AttributeOverride(name = "password", column = Column(name = "password", nullable = false))
    )
    val userInfo: UserInfo = userInfo

    @AttributeOverride(name = "value", column = Column(name = "ref_id"))
    val refId: ReferenceId = refId

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: Status = status
        private set

    @AttributeOverride(name = "value", column = Column(name = "contents"))
    var contents: CommentContents = contents
        private set

    companion object
}
