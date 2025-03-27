package kr.co.jiniaslog.blog.domain.tag

import jakarta.persistence.AttributeOverride
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.Tagging
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAggregate
import kr.co.jiniaslog.shared.core.domain.IdUtils

@Entity
class Tag private constructor(
    id: TagId,
    name: TagName,
) : JpaAggregate<TagId>() {
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

    @OneToMany(mappedBy = "tag", orphanRemoval = true, cascade = [CascadeType.ALL])
    private val _taggings: MutableSet<Tagging> = mutableSetOf()

    val taggingArticles: Set<ArticleId>
        get() = _taggings.map { it.article.entityId }.toSet()

    fun addTagging(tagging: Tagging) {
        _taggings.add(tagging)
    }

    @PreUpdate
    @PrePersist
    fun validate() {}

    companion object {
        fun newOne(name: TagName): Tag {
            return Tag(TagId(IdUtils.generate()), name)
        }
    }
}
