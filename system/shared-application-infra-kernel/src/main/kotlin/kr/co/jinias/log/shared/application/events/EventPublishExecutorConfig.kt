package kr.co.jinias.log.shared.application.events

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
internal class EventPublishExecutorConfig {
    @Bean
    fun domainEventPublishExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.queueCapacity = 100
        executor.setAllowCoreThreadTimeOut(true)
        executor.setThreadNamePrefix("domainEventPublishExecutor-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(300)
        executor.initialize()
        return executor
    }
}
