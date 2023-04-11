package kr.co.jiniaslog.shared.core.domain

interface DomainEventPublisher {
    fun publish(eventName: String, message: Message)
}
