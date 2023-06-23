package kr.co.jiniaslog.blogcore.adapter.persistence.category

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.co.jiniaslog.shared.persistence.BasePersistenceModel
import java.time.LocalDateTime

@Entity
@Table(
    name = "categories",
)
class CategoryPM(
    @Id
    @Column(name = "category_id")
    override val id: Long,

    @Column(nullable = false, length = 50, name = "label")
    var label: String,

    @Column(nullable = true, name = "parent_id")
    var parentId: Long?,

    @Column(nullable = false, name = "orders")
    var order: Int,

    createdDate: LocalDateTime? = null,

    updatedDate: LocalDateTime? = null,
) : BasePersistenceModel(createdDate, updatedDate)
