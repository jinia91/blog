package kr.co.jinias.log.shared.application.events

import kr.co.jiniaslog.shared.core.domain.DomainEventManager
import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import org.springframework.core.Ordered
import org.springframework.transaction.support.TransactionSynchronization

internal class EventsTransactionSynchronization(
    private val domainEventPublisher: DomainEventPublisher,
) : TransactionSynchronization, Ordered {

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }

    override fun afterCommit() {
        DomainEventManager.getDomainEvents().forEach { e ->
            val eventName: String = e.javaClass.simpleName
            domainEventPublisher.publish(eventName, e)
        }
    }

    override fun afterCompletion(status: Int) {
        DomainEventManager.clear()
    }
}
