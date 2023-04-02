package kr.co.jiniaslog.shared.persistence

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BasePM {
    @CreatedDate
    @Column(updatable = false, name = "created_date")
    private val createdDate: LocalDateTime? = null

    @LastModifiedDate
    @Column(name = "updated_date")
    private val updatedDate: LocalDateTime? = null
}
