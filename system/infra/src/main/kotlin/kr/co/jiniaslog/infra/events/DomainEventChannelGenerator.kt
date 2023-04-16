package kr.co.jiniaslog.infra.events

import mu.KotlinLogging
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
internal class DomainEventChannelGenerator : BeanDefinitionRegistryPostProcessor {
    companion object {
        const val ROOT_PACKAGE = "kr.co.jiniaslog"
    }

//    private val pubSubChannelBeanBuilder: BeanDefinitionBuilder = BeanDefinitionBuilder
//        .genericBeanDefinition(PublishSubscribeChannel::class.java)

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {}

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val findAnnotatedEventClasses = DomainEventScanner.findAnnotatedEventClasses(ROOT_PACKAGE)
        findAnnotatedEventClasses.forEach { clazz ->
            val taskExecutor = ThreadPoolTaskExecutor()
            taskExecutor.corePoolSize = 5

            taskExecutor.setThreadNamePrefix("Sub-Worker-")
            taskExecutor.initialize()

            val beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(
                PublishSubscribeChannel::class.java,
            )
            beanDefinitionBuilder.addConstructorArgValue(taskExecutor)

            val channelName = generateChannelBeanName(clazz)
            registry.registerBeanDefinition(channelName, beanDefinitionBuilder.beanDefinition)
            log.info { "register success $channelName" }
        }
    }

    private fun generateChannelBeanName(eventClass: Class<*>): String {
        return eventClass.simpleName + "Channel"
    }
}
