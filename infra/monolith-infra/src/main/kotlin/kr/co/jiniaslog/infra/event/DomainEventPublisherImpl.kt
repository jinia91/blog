package kr.co.jiniaslog.infra.event

import kr.co.jiniaslog.shared.core.domain.DomainEvent
import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import org.springframework.context.ApplicationContext
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Component

private val log = mu.KotlinLogging.logger { }

@Component
internal class DomainEventPublisherImpl(
    private val applicationContext: ApplicationContext,
) : DomainEventPublisher {
    override suspend fun publish(event: DomainEvent) {
        log.info { "publish event : $event" }
        val message =
            MessageBuilder.withPayload(event)
                .setHeader(DomainEvent.EVENT_ID, event.id)
                .build()
        resolveChannel(event).send(message)
    }

    private fun resolveChannel(event: DomainEvent) = (applicationContext.getBean(event.channelName) as MessageChannel)
}
