package kr.co.jiniaslog.shared.core.domain

abstract class Repository<T : AggregateRoot<I>, I : ValueObject> {
    abstract val eventPublisher: EventPublisher
    abstract suspend fun findById(id: I): T
    abstract suspend fun findAll(): List<T>?
    abstract suspend fun deleteById(id: I)
    suspend fun save(entity: T): T {
        val savedEntity = saveInternal(entity)
        eventPublisher.publishEvent(entity.getEvents())
        entity.clearEvents()
        return savedEntity
    }
    protected abstract suspend fun saveInternal(entity: T): T
}

