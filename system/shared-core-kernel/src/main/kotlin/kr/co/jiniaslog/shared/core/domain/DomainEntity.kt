package kr.co.jiniaslog.shared.core.domain

abstract class DomainEntity<out T : ValueObject> {

    abstract val id: T

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainEntity<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
