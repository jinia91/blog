package kr.co.jiniaslog.shared.core.domain

abstract class AggregateRoot<T : ValueObject> : DomainEntity<T>() {
    protected fun registerEvent(event: DomainEvent): DomainEvent {
        DomainEventManager.register(event)
        return event
    }

    protected fun clearEvents() = DomainEventManager.clear()

    protected fun getEvents() = DomainEventManager.getDomainEvents()
}
