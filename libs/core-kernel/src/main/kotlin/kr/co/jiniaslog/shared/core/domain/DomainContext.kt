package kr.co.jiniaslog.shared.core.domain

import java.util.concurrent.ConcurrentLinkedQueue

data class DomainContext(
    private var events: ConcurrentLinkedQueue<DomainEvent> = ConcurrentLinkedQueue(),
) {
    fun add(event: DomainEvent) {
        events.add(event)
    }

    fun toListAndClear(): List<DomainEvent> {
        return synchronized(this) {
            events.toList().also {
                this.clear()
            }
        }
    }

    fun clear() {
        events.clear()
    }

    companion object {
        const val DOMAIN_EVENT_KEY = "DomainContext"
    }
}
