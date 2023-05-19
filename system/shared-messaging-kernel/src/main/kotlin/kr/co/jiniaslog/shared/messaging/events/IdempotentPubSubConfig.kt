package kr.co.jiniaslog.shared.messaging.events

import kr.co.jiniaslog.shared.core.domain.DomainEvent
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.handler.advice.IdempotentReceiverInterceptor
import org.springframework.integration.metadata.ConcurrentMetadataStore
import org.springframework.integration.selector.MetadataStoreSelector

private val log = KotlinLogging.logger { }

@Configuration
class IdempotentPubSubConfig {

    @Bean
    fun localMetadataStoreSelector(metadataStore: ConcurrentMetadataStore) =
        MetadataStoreSelector({
            ("EventId:${it.headers[DomainEvent.EVENT_ID]}").also {
                log.info { "idempotent check { eventId: $it }" }
            }
        }, {
            it.payload.toString()
        }, metadataStore)

    @Bean
    fun localIdempotentReceiverInterceptor(localMetadataStoreSelector: MetadataStoreSelector): IdempotentReceiverInterceptor {
        return IdempotentReceiverInterceptor(localMetadataStoreSelector).apply {
            setThrowExceptionOnRejection(true)
        }
    }
}
