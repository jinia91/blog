package kr.co.jiniaslog.memo.adapter.out.rdb

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoStatus
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPersistenceModel
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.Optional

@Entity
@Table(name = "memo")
class MemoPM(
    @Id
    @Column(name = "memo_id")
    override val id: Long,
    @Column(name = "title")
    var title: String,
    @Column(name = "content")
    var content: String,
    @Column(name = "status")
    var status: String,
    @OneToMany(mappedBy = "memoId")
    @Cascade(CascadeType.ALL)
    var tagging: List<TaggingPM>,
    @OneToMany(mappedBy = "rootId")
    @Cascade(CascadeType.ALL)
    var links: List<MemoLinkPM>,
    createdAt: LocalDateTime? = null,
    updatedAt: LocalDateTime? = null,
) : AbstractPersistenceModel<Memo>(createdAt, updatedAt) {
    fun toDomain(): Memo {
        return Memo.from(
            id = MemoId(id),
            title = MemoTitle(title),
            content = MemoContent(content),
            tags = tagging.map { it.toDomain() },
            links = links.map { it.toDomain() },
            status = MemoStatus.valueOf(status),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}

fun Memo.toPm(): MemoPM {
    return MemoPM(
        id = this.id.value,
        title = this.title.value,
        content = this.content.value,
        status = this.status.name,
        tagging = this.tags.map { it.toPm() },
        links = this.links.map { it.toPm() },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

interface MemoPMRepository : JpaRepository<MemoPM, Long> {
    @EntityGraph(attributePaths = ["tagging", "links"])
    override fun findById(id: Long): Optional<MemoPM>

    @EntityGraph(attributePaths = ["tagging", "links"])
    override fun findAll(): List<MemoPM>
}
