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
    categoryId: CategoryId,
    categoryTitle: CategoryTitle,
    sortingPoint: Int,
    parent: Category?
) : AggregateRoot<CategoryId>() {
    @EmbeddedId
    @AttributeOverride(
        column = Column(name = "category_id"),
        name = "value",
    )
    override val id: CategoryId = categoryId

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

    init {
        require(sortingPoint >= 0) { "sortingPoint는 0 이상이어야 합니다" }
    }

    fun edit(
        categoryTitle: CategoryTitle,
        depth: Int,
        sortingPoint: Int,
        parent: Category?
    ) {
        this.categoryTitle = categoryTitle
        this.sortingPoint = sortingPoint
        parent?.let {
            require(parent.id != this.id) { "부모 카테고리는 자기 자신이 될 수 없습니다" }
            this.parent = parent
            parent.addChild(this)
        }
    }

    private fun addChild(child: Category) {
        this.children.add(child)
    }
}
