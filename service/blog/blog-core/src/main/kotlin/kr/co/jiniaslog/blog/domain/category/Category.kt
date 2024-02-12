package kr.co.jiniaslog.blog.domain.category

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import kr.co.jiniaslog.shared.core.domain.AggregateRoot

@Entity
class Category(
    categoryId: CategoryId,
    categoryTitle: CategoryTitle,
    depth: Int,
    sortingPoint: Int,
) : AggregateRoot<CategoryId>() {
    @Id
    @Column(name = "category_id")
    override val id: CategoryId = categoryId

    @Column(name = "category_title")
    var categoryTitle: CategoryTitle = categoryTitle
        private set

    @ManyToOne
    @JoinColumn(name = "parent_id")
    var parent: Category? = null
        private set

    @OneToMany(mappedBy = "parent")
    var children: MutableList<Category> = mutableListOf()
        private set

    var depth: Int = depth
        private set

    var sortingPoint: Int = sortingPoint
        private set

    fun changeTitle(categoryTitle: CategoryTitle) {
        this.categoryTitle = categoryTitle
    }

    fun edit(
        categoryTitle: CategoryTitle,
        depth: Int,
        sortingPoint: Int,
    ) {
        require(sortingPoint >= 0) { "sortingPoint must be positive" }
        require(depth >= 0) { "dept must be positive" }
        this.categoryTitle = categoryTitle
        this.sortingPoint = sortingPoint
        this.depth = depth
    }

    fun setParent(parent: Category) {
        require(parent.id != this.id) { "parent must be different from self" }
        require(parent.parent == null) { "parent must be root" }
        this.parent = parent
        parent.addChild(this)
    }

    private fun addChild(child : Category) {
        this.children.add(child)
    }
}
