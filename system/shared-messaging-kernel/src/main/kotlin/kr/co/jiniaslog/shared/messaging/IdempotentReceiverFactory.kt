package kr.co.jiniaslog.shared.messaging

import kr.co.jiniaslog.shared.core.domain.DomainEvent
import mu.KotlinLogging
import org.springframework.integration.handler.advice.IdempotentReceiverInterceptor
import org.springframework.integration.metadata.ConcurrentMetadataStore
import org.springframework.integration.selector.MetadataStoreSelector
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
class IdempotentReceiverFactory(
    private val metadataStore: ConcurrentMetadataStore,
) {
    fun buildIdempotentReceiverInterceptor(subscriberName: String): IdempotentReceiverInterceptor {
        val localMetadataStoreSelector = MetadataStoreSelector({
            ("EventId:${it.headers[DomainEvent.EVENT_ID]}:Sub:$subscriberName").also {
                log.info { "idempotent check { eventId: $it }" }
            }
        }, {
            it.payload.toString()
        }, metadataStore)

        return IdempotentReceiverInterceptor(localMetadataStoreSelector).apply {
            setThrowExceptionOnRejection(true)
        }
    }
}
