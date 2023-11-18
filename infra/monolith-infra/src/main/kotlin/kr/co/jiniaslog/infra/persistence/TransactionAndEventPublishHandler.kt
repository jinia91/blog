package kr.co.jiniaslog.infra.persistence

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import kr.co.jiniaslog.shared.core.domain.EventContextManager
import kr.co.jiniaslog.shared.core.domain.TransactionHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

private val log = mu.KotlinLogging.logger { }

@Component
class TransactionAndEventPublishHandler(
    private val transactionalOperator: TransactionalOperator,
    private val domainEventPublisher: DomainEventPublisher,
    private val domainContextManager: EventContextManager,
) : TransactionHandler {
    override suspend fun <T> runInRepeatableReadTransaction(supplier: suspend () -> T): T {
        return coroutineScope {
            val t =
                transactionalOperator.executeAndAwait {
                    supplier.invoke()
                }
            launch {
                domainContextManager.getDomainEventsAndClear()
                    .forEach { launch { domainEventPublisher.publish(it) } }
            }
            t
        }
    }
}
