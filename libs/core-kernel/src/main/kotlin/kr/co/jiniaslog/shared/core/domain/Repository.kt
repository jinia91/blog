package kr.co.jiniaslog.shared.core.domain

interface Repository<T : AggregateRoot<I>, I : ValueObject> {
    suspend fun nextId(): I

    suspend fun findById(id: I): T?

    suspend fun findAll(): List<T>

    suspend fun deleteById(id: I)

    suspend fun save(entity: T): T
}
