package kr.co.jiniaslog.blog.domain.category

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import kr.co.jiniaslog.shared.core.domain.AggregateRoot

@Entity
class Category private constructor(
    categoryId: CategoryId,
    categoryTitle: CategoryTitle,
    parent: Category?,
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
    var parent: Category? = parent
        private set

    @OneToMany(mappedBy = "parent")
    var child: List<Category> = ArrayList<Category>()
        private set

    var sortingPoint: Int = sortingPoint
        private set

    fun changeTitle(categoryTitle: CategoryTitle) {
        this.categoryTitle = categoryTitle
    }

    fun changeHierarchy(
        parent: Category,
        sortingPoint: Int,
    ) {
        require(sortingPoint > 0) { "sortingPoint must be positive" }
        require(parent.id != this.id) { "parent must be different from self" }
        require(parent.parent == null) { "parent must be root" }
        this.parent = parent
        this.sortingPoint = sortingPoint
    }
}
