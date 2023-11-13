package kr.co.jiniaslog.shared.core.domain

interface ValueObject {
    fun validate()
}

interface ValueObjectId : ValueObject {
    val value: Long
}
