package kr.co.jiniaslog.shared.core.domain

interface Repository<T : AggregateRoot<I>, I : ValueObjectId> {
    suspend fun nextId(): I

    suspend fun findById(id: I, mode: FetchMode): T?

    suspend fun findAll(mode: FetchMode): List<T>

    suspend fun deleteById(id: I)

    suspend fun save(entity: T): T
}

enum class FetchMode {
    ALL, NONE
}