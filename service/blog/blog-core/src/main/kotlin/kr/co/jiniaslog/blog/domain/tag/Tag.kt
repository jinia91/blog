package kr.co.jiniaslog.blog.domain.tag

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import kr.co.jiniaslog.shared.core.domain.AggregateRoot

@Entity
class Tag private constructor(
    id: TagId,
    name: TagName,
) : AggregateRoot<TagId>() {
    @Id
    @Column(name = "tag_id")
    override val id: TagId = id

    @Column(name = "tag_name")
    val tagName: TagName = name
}
