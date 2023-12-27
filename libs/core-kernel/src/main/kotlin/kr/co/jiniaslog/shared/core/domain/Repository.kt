package kr.co.jiniaslog.shared.core.domain

interface Repository<T : AggregateRoot<I>, I : ValueObject> {
    fun findById(id: I): T?

    fun findAll(): List<T>

    fun deleteById(id: I)

    fun save(entity: T): T
}
