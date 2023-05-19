package kr.co.jiniaslog.shared.messaging.events

import kr.co.jiniaslog.shared.core.domain.DomainEventManager
import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import mu.KotlinLogging
import org.springframework.core.Ordered
import org.springframework.transaction.support.TransactionSynchronization

private val log = KotlinLogging.logger { }

internal class EventsTransactionSynchronization(
    private val domainEventPublisher: DomainEventPublisher,
) : TransactionSynchronization, Ordered {

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }

    override fun afterCommit() {
        DomainEventManager.getDomainEvents()
            .forEach { domainEventPublisher.publish(it) }
    }

    override fun afterCompletion(status: Int) {
        DomainEventManager.clear()
    }
}
