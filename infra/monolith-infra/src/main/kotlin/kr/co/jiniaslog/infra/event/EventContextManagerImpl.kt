package kr.co.jiniaslog.infra.event

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.reactor.ReactorContext
import kr.co.jiniaslog.infra.event.DomainContext.Companion.DOMAIN_EVENT_KEY
import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import kr.co.jiniaslog.shared.core.domain.DomainEvent
import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import kr.co.jiniaslog.shared.core.domain.EventContextManager
import kr.co.jiniaslog.shared.core.domain.EventManger

@CustomComponent
internal class EventContextManagerImpl(
    private var eventPublisher: DomainEventPublisher,
) : EventContextManager {
    init {
        EventManger.eventContextManager = this
    }

    override suspend fun add(event: DomainEvent) {
        val events = getContext()
        events.add(event)
    }

    override suspend fun clear() {
        val events = getContext()
        events.clear()
    }

    override suspend fun getDomainEventsAndClear(): List<DomainEvent> {
        val events = getContext()
        return events.toListAndClear()
    }

    private suspend fun getContext() = currentCoroutineContext()[ReactorContext]?.context?.get<DomainContext>(DOMAIN_EVENT_KEY)!!
}
