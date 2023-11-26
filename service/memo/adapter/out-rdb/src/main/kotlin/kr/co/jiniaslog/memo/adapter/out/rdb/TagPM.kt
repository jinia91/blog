package kr.co.jiniaslog.memo.adapter.out.rdb

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.co.jiniaslog.memo.domain.tag.Tag
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPersistenceModel
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

@Entity
@Table(name = "tag")
class TagPM(
    @Id
    override val id: Long,
    @Column(name = "name")
    var name: String,
    createdAt: LocalDateTime? = null,
    updatedAt: LocalDateTime? = null,
) : AbstractPersistenceModel<Tag>(createdAt, updatedAt) {
    fun toDomain(): Tag {
        return Tag.from(
            id = TagId(id),
            name = TagName(name),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}

fun Tag.toPm(): TagPM {
    return TagPM(
        id = this.id.value,
        name = this.name.value,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

interface TagPMRepository : JpaRepository<TagPM, Long> {
    fun findByName(name: String): TagPM?
}
