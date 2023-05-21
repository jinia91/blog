package kr.co.jiniaslog.infra.events

import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronizationManager

@Aspect
@Component
internal class EventsTransactionSynchronizationAspect(
    private val domainEventPublisher: DomainEventPublisher,
) {
    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    fun before(joinPoint: JoinPoint) = ensureEventsTransactionSynchronizationRegistered()

    private fun ensureEventsTransactionSynchronizationRegistered() {
        findEventsTransactionSynchronization() ?: run {
            val newSynchronization = EventsTransactionSynchronization(domainEventPublisher)
            registerEventsTransactionSynchronization(newSynchronization)
        }
    }

    private fun findEventsTransactionSynchronization(): EventsTransactionSynchronization? =
        TransactionSynchronizationManager.getSynchronizations()
            .firstOrNull { it is EventsTransactionSynchronization } as? EventsTransactionSynchronization

    private fun registerEventsTransactionSynchronization(synchronization: EventsTransactionSynchronization) =
        TransactionSynchronizationManager.registerSynchronization(synchronization)
}
