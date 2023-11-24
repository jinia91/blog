package kr.co.jiniaslog.shared.core.domain

private val log = mu.KotlinLogging.logger {}

abstract class AggregateRoot<out T : ValueObject> : DomainEntity<T>() {
    private val eventManager = EventManger

    protected fun registerEvent(event: DomainEvent): DomainEvent {
        log.debug { "register event: $event" }
        eventManager.add(event)
        return event
    }

    fun clearEvents() = eventManager.clear()

    fun getEvents() = eventManager.toListAndClear()
}
