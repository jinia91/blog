package kr.co.jiniaslog.infra.event

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.reactor.ReactorContext
import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import kr.co.jiniaslog.shared.core.domain.DomainContext
import kr.co.jiniaslog.shared.core.domain.DomainEvent
import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import kr.co.jiniaslog.shared.core.domain.EventContextManager
import kr.co.jiniaslog.shared.core.domain.EventManger

private val log = mu.KotlinLogging.logger {}

@CustomComponent
internal class EventContextManagerImpl(
    private var eventPublisher: DomainEventPublisher,
) : EventContextManager {
    init {
        EventManger.eventContextManager = this
    }

    override suspend fun add(event: DomainEvent) {
        val events = getContext()
        events.add(event).also {
            log.debug { "currentDomainContext : $it" }
        }
    }

    override suspend fun clear() {
        val events = getContext()
        events.clear()
    }

    override suspend fun getDomainEventsAndClear(): List<DomainEvent> {
        val events = getContext()
        return events.toListAndClear()
    }

    private suspend fun getContext() =
        currentCoroutineContext()[DomainContext.key]
            ?: currentCoroutineContext()[ReactorContext.Key]?.context?.get(DomainContext.key)!!
}
