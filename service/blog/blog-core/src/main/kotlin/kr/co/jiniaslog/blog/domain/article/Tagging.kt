package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.shared.core.domain.DomainEntity

@Entity
class Tagging internal constructor(
    id: TaggingId,
    article: Article,
    tag: Tag
) : DomainEntity<TaggingId>() {
    @EmbeddedId
    @AttributeOverride(column = Column(name = "id"), name = "value")
    override val entityId: TaggingId = id

    @ManyToOne
    @AttributeOverride(column = Column(name = "article_id"), name = "value")
    val article: Article = article

    @ManyToOne
    @AttributeOverride(column = Column(name = "tag_id"), name = "value")
    val tag: Tag = tag
}
