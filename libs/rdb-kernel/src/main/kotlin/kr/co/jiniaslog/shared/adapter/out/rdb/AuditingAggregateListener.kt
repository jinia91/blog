package kr.co.jiniaslog.shared.adapter.out.rdb

import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.springframework.beans.factory.annotation.Configurable
import java.time.LocalDateTime

@Configurable
class AuditingAggregateListener {
    @PrePersist
    fun touchForCreate(target: Any) {
        if (target is JpaAggregate<*>) {
            target.createdAt = LocalDateTime.now()
            target.updatedAt = LocalDateTime.now()
        }
    }

    @PreUpdate
    fun touchForUpdate(target: Any) {
        if (target is JpaAggregate<*>) {
            target.updatedAt = LocalDateTime.now()
        }
    }
}
