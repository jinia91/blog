package kr.co.jiniaslog.shared.core.domain

import mu.KotlinLogging

private val log = KotlinLogging.logger { }

object DomainEventManager {
    private val eventsThreadLocal = ThreadLocal.withInitial<MutableList<DomainEvent>> { mutableListOf() }

    fun register(event: DomainEvent) {
        eventsThreadLocal.get().add(event)
        log.info { "register Event : $event" }
    }

    fun getDomainEvents(): List<DomainEvent> {
        return eventsThreadLocal.get().toList()
    }

    fun clear() {
        eventsThreadLocal.get().clear()
    }

    fun isEmpty(): Boolean = eventsThreadLocal.get().isEmpty()
}
