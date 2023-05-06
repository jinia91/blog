package kr.co.jiniaslog.infra.events

import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import kr.co.jiniaslog.shared.core.domain.Message
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.core.task.TaskExecutor
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
internal class DomainEventPublisherImpl(
    private val applicationContext: ApplicationContext,
    @param:Qualifier("domainEventPublishExecutor") private val taskExecutor: TaskExecutor,
) : DomainEventPublisher {
    override fun publish(event: Message) = taskExecutor.execute {
        log.info { "publish event : $event" }
        val eventName: String = event::class.simpleName!!
        resolveChannel(eventName).send(GenericMessage(event))
    }

    private fun resolveChannel(eventName: String) = (applicationContext.getBean(eventName + "Channel") as MessageChannel)
}
