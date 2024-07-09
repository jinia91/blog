package kr.co.jiniaslog.blog.domain.category

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import kr.co.jiniaslog.shared.core.domain.AggregateRoot

@Entity
class Category(
    id: CategoryId,
    categoryTitle: CategoryTitle,
    sortingPoint: Int,
    parent: Category?
) : AggregateRoot<CategoryId>() {
    @EmbeddedId
    @AttributeOverride(
        column = Column(name = "category_id"),
        name = "value",
    )
    override val id: CategoryId = id

    @AttributeOverride(
        column = Column(name = "category_title"),
        name = "value",
    )
    var categoryTitle: CategoryTitle = categoryTitle
        private set

    @ManyToOne
    @JoinColumn(name = "parent_id")
    var parent: Category? = parent
        private set

    @OneToMany(mappedBy = "parent")
    var children: MutableList<Category> = mutableListOf()
        private set

    var sortingPoint: Int = sortingPoint
        private set

    val isChild: Boolean
        get() = parent != null

    init {
        require(sortingPoint >= 0) { "sortingPoint는 0 이상이어야 합니다" }
    }
}
