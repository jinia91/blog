package kr.co.jiniaslog.shared.core.domain

object DomainEventManager {
    private val eventsThreadLocal = ThreadLocal.withInitial<MutableList<DomainEvent>> { mutableListOf() }

    fun register(event: DomainEvent) {
        eventsThreadLocal.get().add(event)
    }

    fun getDomainEvents(): List<DomainEvent> {
        return eventsThreadLocal.get().toList()
    }

    fun clear() {
        eventsThreadLocal.get().clear()
    }

    fun isEmpty(): Boolean = eventsThreadLocal.get().isEmpty()
}
