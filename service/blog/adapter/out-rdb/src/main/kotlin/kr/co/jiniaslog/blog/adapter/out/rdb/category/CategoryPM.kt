package kr.co.jiniaslog.blog.adapter.out.rdb.category

import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryName
import kr.co.jiniaslog.blog.domain.category.SortingOrder
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPM
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "category")
class CategoryPM(
    @Id
    override val id: Long,
    var name: String,
    var order: Int,
    var parentId: Long?,
    override var updatedAt: LocalDateTime?,
    override var createdAt: LocalDateTime?,
) : AbstractPM() {
    fun toDomain() =
        Category.from(
            id = CategoryId(id),
            name = CategoryName(name),
            order = SortingOrder(order),
            parentCategoryId = parentId?.let { CategoryId(it) },
            updatedAt = updatedAt,
            createdAt = createdAt,
        )
}

internal fun Category.toPM() =
    CategoryPM(
        id = this.id.value,
        name = this.name.value,
        order = this.order.value,
        parentId = this.parentCategoryId?.value,
        updatedAt = this.updatedAt,
        createdAt = this.createdAt,
    )
