package kr.co.jiniaslog.shared.core.domain

interface DomainEventPublisher {
    suspend fun publish(event: DomainEvent)
}
