package kr.co.jiniaslog.shared.messaging.events

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
internal class EventPublishExecutorConfig {
    @Bean
    fun domainEventPublishExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.queueCapacity = 100
        executor.setAllowCoreThreadTimeOut(true)
        executor.setThreadNamePrefix("Pub-Worker-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(300)
        executor.initialize()
        return executor
    }
}
