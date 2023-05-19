package kr.co.jiniaslog.shared.messaging.events

import kr.co.jiniaslog.shared.core.domain.DomainEvent
import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.core.task.TaskExecutor
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
internal class DomainEventPublisherImpl(
    private val applicationContext: ApplicationContext,
    @param:Qualifier("domainEventPublishExecutor") private val taskExecutor: TaskExecutor,
) : DomainEventPublisher {
    override fun publish(event: DomainEvent) = taskExecutor.execute {
        log.info { "publish event : $event" }
        val eventName: String = event::class.simpleName!!
        val message = MessageBuilder.withPayload(event)
            .setHeader(DomainEvent.EVENT_ID, event.id)
            .build()
        resolveChannel(eventName).send(message)
    }

    private fun resolveChannel(eventName: String) = (applicationContext.getBean(eventName + "Channel") as MessageChannel)
}
