package kr.co.jiniaslog.shared.core.domain

object EventManger {
    lateinit var eventContextManager: EventContextManager

    suspend fun add(event: DomainEvent) {
        eventContextManager.add(event)
    }

    suspend fun clear() {
        eventContextManager.clear()
    }

    suspend fun toListAndClear(): List<DomainEvent> {
        return eventContextManager.getDomainEventsAndClear()
    }
}

interface EventContextManager {
    suspend fun add(event: DomainEvent)

    suspend fun clear()

    suspend fun getDomainEventsAndClear(): List<DomainEvent>
}
