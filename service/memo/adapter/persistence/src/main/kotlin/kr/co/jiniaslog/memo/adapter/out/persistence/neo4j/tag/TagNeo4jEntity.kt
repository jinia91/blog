package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.tag

import kr.co.jiniaslog.memo.domain.tag.Tag
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import java.time.LocalDateTime

@Node
class TagNeo4jEntity(
    @Id
    val id: Long,
    @Property("name")
    val name: String,
    @CreatedDate
    var createdAt: LocalDateTime?,
    @LastModifiedDate
    var updatedAt: LocalDateTime?,
) : Persistable<Long> {
    override fun isNew(): Boolean = createdAt == null

    override fun getId(): Long = id

    fun toDomain(): Tag {
        return Tag.from(
            id = TagId(this.id),
            name = TagName(this.name),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )
    }
}
