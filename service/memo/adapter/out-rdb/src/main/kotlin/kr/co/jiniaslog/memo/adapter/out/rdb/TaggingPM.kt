package kr.co.jiniaslog.memo.adapter.out.rdb

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTagId
import kr.co.jiniaslog.memo.domain.memo.Tagging
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPersistenceModel
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

@Entity
@Table(name = "tagging")
class TaggingPM(
    @Id
    override val id: Long,
    @Column(name = "memo_id")
    var memoId: Long,
    @Column(name = "tag_id")
    var tagId: Long,
    createdAt: LocalDateTime? = null,
    updatedAt: LocalDateTime? = null,
) : AbstractPersistenceModel<Tagging>(createdAt, updatedAt) {
    fun toDomain(): Tagging {
        return Tagging.from(
            id = MemoTagId(id),
            memoId = MemoId(memoId),
            tagId = TagId(tagId),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}

fun Tagging.toPm(): TaggingPM {
    return TaggingPM(
        id = id.value,
        memoId = memoId.value,
        tagId = tagId.value,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

interface TaggingPMRepository : JpaRepository<TaggingPM, Long>
