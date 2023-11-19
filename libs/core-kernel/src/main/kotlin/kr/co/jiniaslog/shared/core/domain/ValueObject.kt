package kr.co.jiniaslog.shared.core.domain

interface ValueObject {
    fun validate()

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}
