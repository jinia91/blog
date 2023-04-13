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
abstract class BasePM : Persistable<Long> {
    abstract val id: Long

    @CreatedDate
    @Column(updatable = false, name = "created_date")
    open var createdDate: LocalDateTime? = null

    @LastModifiedDate
    @Column(name = "updated_date")
    open var updatedDate: LocalDateTime? = null

    override fun isNew(): Boolean = createdDate == null

    override fun getId(): Long? = id
}
