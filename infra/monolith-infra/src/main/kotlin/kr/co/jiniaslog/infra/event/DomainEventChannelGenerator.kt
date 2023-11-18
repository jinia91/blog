package kr.co.jiniaslog.infra.event

import kr.co.jiniaslog.shared.core.domain.DomainEvent
import mu.KotlinLogging
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
internal class DomainEventChannelGenerator : BeanDefinitionRegistryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {}

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val findEventClasses = DomainEventScanner.findEventClasses(ROOT_PACKAGE)
        findEventClasses.forEach { clazz ->
            val pubsubChannelDefinition = createBeanDefinitionBuilder()
            val channelName = generateChannelBeanName(clazz)
            registry.registerBeanDefinition(channelName, pubsubChannelDefinition.beanDefinition)
            log.info { "register success $channelName" }
        }
    }

    private fun createBeanDefinitionBuilder(): BeanDefinitionBuilder {
        return BeanDefinitionBuilder.genericBeanDefinition(
            PublishSubscribeChannel::class.java,
        )
    }

    private fun generateChannelBeanName(eventClass: Class<*>): String {
        return eventClass.simpleName + DomainEvent.CHANNEL_NAME_CONVENTION
    }

    companion object {
        const val ROOT_PACKAGE = "kr.co.jiniaslog"
    }
}
