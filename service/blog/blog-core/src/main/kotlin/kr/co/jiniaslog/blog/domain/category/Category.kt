package kr.co.jiniaslog.blog.domain.category

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import kr.co.jiniaslog.shared.core.domain.AggregateRoot

/**
 * 카테고리 어그리게이트 루트
 *
 * - 구현 편의상 부모 카테고리와 자식카테고리 2계층으로만 구현한다
 */
@Entity
class Category(
    id: CategoryId,
    categoryTitle: CategoryTitle,
    sortingPoint: Int,
) : AggregateRoot<CategoryId>() {
    @EmbeddedId
    @AttributeOverride(
        column = Column(name = "category_id"),
        name = "value",
    )
    override val entityId: CategoryId = id

    @AttributeOverride(
        column = Column(name = "category_title"),
        name = "value",
    )
    var categoryTitle: CategoryTitle = categoryTitle
        private set

    @ManyToOne
    @JoinColumn(name = "parent_id")
    var parent: Category? = null
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

    fun changeParent(parent: Category?) {
        this.parent = parent
        parent?.children?.add(this)
    }

    fun sync(
        categoryTitle: CategoryTitle,
        sortingPoint: Int,
        parent: Category?,
    ) {
        require(sortingPoint >= 0) { "sortingPoint는 0 이상이어야 합니다" }
        this.categoryTitle = categoryTitle
        this.sortingPoint = sortingPoint
        this.parent = parent
    }
}
