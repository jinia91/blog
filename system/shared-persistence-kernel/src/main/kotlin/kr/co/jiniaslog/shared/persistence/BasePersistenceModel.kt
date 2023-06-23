package kr.co.jiniaslog.shared.persistence

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BasePersistenceModel(
    @CreatedDate
    @Column(updatable = false, name = "created_date")
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_date")
    var updatedDate: LocalDateTime? = null,
) : Persistable<Long> {
    abstract val id: Long

    @Transient
    var isNewFlag: Boolean = false

    override fun isNew(): Boolean = isNewFlag

    override fun getId(): Long? = id
}
