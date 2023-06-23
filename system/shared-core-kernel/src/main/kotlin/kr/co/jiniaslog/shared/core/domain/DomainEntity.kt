package kr.co.jiniaslog.shared.core.domain

import java.time.LocalDateTime

abstract class DomainEntity<out T : ValueObject>(
    var createdDate: LocalDateTime?,
    var updatedDate: LocalDateTime?,
) {

    abstract val id: T

    fun syncAuditAfterPersist(createdDate: LocalDateTime, updatedDate: LocalDateTime) {
        this.createdDate = createdDate
        this.updatedDate = updatedDate
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainEntity<*>

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
