package kr.co.jiniaslog.shared.core.domain

private val log = mu.KotlinLogging.logger {}

abstract class AggregateRoot<out T : ValueObject> : DomainEntity<T>() {
    private val eventManager = EventManger

    protected suspend fun registerEvent(event: DomainEvent): DomainEvent {
        log.debug { "register event: $event" }
        eventManager.add(event)
        return event
    }

    suspend fun clearEvents() = eventManager.clear()

    suspend fun getEvents() = eventManager.toListAndClear()
}
