package kr.co.jiniaslog.shared.core.domain

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.time.LocalDateTime

abstract class DomainEntity<out T : ValueObject>(
    open var createdAt: LocalDateTime? = null,
    open var updatedAt: LocalDateTime? = null,
) {
    abstract val entityId: T

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainEntity<*>

        return entityId == other.entityId
    }

    override fun hashCode(): Int {
        return entityId.hashCode()
    }
}
