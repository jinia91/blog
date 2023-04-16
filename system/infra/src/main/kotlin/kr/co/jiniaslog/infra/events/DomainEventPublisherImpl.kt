package kr.co.jiniaslog.infra.events

import kr.co.jiniaslog.shared.core.domain.DomainEventPublisher
import kr.co.jiniaslog.shared.core.domain.Message
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.core.task.TaskExecutor
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component

@Component
internal class DomainEventPublisherImpl(
    private val applicationContext: ApplicationContext,
    @param:Qualifier("domainEventPublishExecutor") private val taskExecutor: TaskExecutor,
) : DomainEventPublisher {
    override fun publish(eventName: String, message: Message) = taskExecutor.execute {
        resolveChannel(eventName).send(GenericMessage(message))
    }

    private fun resolveChannel(eventName: String) = (applicationContext.getBean(eventName + "Channel") as MessageChannel)
}
