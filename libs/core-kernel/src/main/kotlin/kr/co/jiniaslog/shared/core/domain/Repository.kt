package kr.co.jiniaslog.shared.core.domain

interface Repository<T : AggregateRoot<I>, I : ValueObject> {
    fun nextId(): I

    fun findById(id: I): T?

    fun findAll(): List<T>

    fun deleteById(id: I)

    fun save(entity: T): T
}

enum class FetchMode {
    ALL,
    NONE,
}
