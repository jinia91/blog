package kr.co.jiniaslog.memo.adapter.out.rdb

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoLink
import kr.co.jiniaslog.memo.domain.memo.MemoLinkId
import kr.co.jiniaslog.memo.domain.memo.MemoLinkType
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPersistenceModel
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

@Entity
@Table(name = "memo_link")
class MemoLinkPM(
    @Id
    override val id: Long,
    @Column(name = "root_id")
    var rootId: Long,
    @Column(name = "linked_id")
    var linkedMemoId: Long,
    @Column(name = "link_type")
    var linkType: String,
    createdAt: LocalDateTime? = null,
    updatedAt: LocalDateTime? = null,
) : AbstractPersistenceModel<MemoLink>(createdAt, updatedAt) {
    fun toDomain(): MemoLink {
        return MemoLink.from(
            id = MemoLinkId(id),
            rootId = MemoId(rootId),
            linkedMemoId = MemoId(linkedMemoId),
            linkType = MemoLinkType.valueOf(linkType),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}

fun MemoLink.toPm(): MemoLinkPM {
    return MemoLinkPM(
        id = this.id.value,
        rootId = this.rootId.value,
        linkedMemoId = this.linkedMemoId.value,
        linkType = this.linkType.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

interface MemoLinkPMRepository : JpaRepository<MemoLinkPM, Long>
