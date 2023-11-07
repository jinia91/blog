package kr.co.jiniaslog.shared.core.domain

import java.time.LocalDateTime

abstract class DomainEntity<out T : ValueObject>(
    val createdDate: LocalDateTime? = null,
    val updatedDate: LocalDateTime? = null,
) {
    abstract val id: T

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
