package kr.co.jiniaslog.blog.domain.tag

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.springframework.data.domain.Persistable

@Entity
class Tag private constructor(
    id: TagId,
    name: TagName,
) : AggregateRoot<TagId>(), Persistable<TagId> {
    @EmbeddedId
    @AttributeOverride(
        column = Column(name = "tag_id"),
        name = "value",
    )
    override val entityId: TagId = id

    @AttributeOverride(
        column = Column(name = "tag_name"),
        name = "value",
    )
    val tagName: TagName = name

    @PreUpdate
    @PrePersist
    fun validate() {}

    companion object {
        fun newOne(name: TagName): Tag {
            return Tag(TagId(IdUtils.generate()), name)
        }
    }

    override fun getId(): TagId {
        return entityId
    }

    override fun isNew(): Boolean {
        return isPersisted.not()
    }
}
