package kr.co.jiniaslog.shared.core.domain

abstract class AggregateRoot<out T : ValueObject> : DomainEntity<T>() {
    private val events = mutableListOf<DomainEvent>()

    protected fun registerEvent(event: DomainEvent): DomainEvent {
        events.add(event)
        return event
    }

    fun clearEvents() = events.clear()

    fun getEvents() = events.toList()
}