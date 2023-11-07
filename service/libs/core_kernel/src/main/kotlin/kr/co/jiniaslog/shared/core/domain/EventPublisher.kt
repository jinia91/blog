package kr.co.jiniaslog.shared.core.domain

interface EventPublisher {
    suspend fun publishEvent(events: List<DomainEvent>)
}