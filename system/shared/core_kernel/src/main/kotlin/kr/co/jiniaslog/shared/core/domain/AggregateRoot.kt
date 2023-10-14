package kr.co.jiniaslog.shared.core.domain

import java.time.LocalDateTime

abstract class AggregateRoot<T : ValueObject>(
    createdDate: LocalDateTime?,
    updatedDate: LocalDateTime?,
) : DomainEntity<T>(createdDate, updatedDate) {
    protected fun registerEvent(event: DomainEvent): DomainEvent {
        DomainEventManager.register(event)
        return event
    }

    protected fun clearEvents() = DomainEventManager.clear()

    protected fun getEvents() = DomainEventManager.getDomainEvents()
}
