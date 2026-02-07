package kr.co.jiniaslog.ai.adapter.inbound.message

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

@Configuration
@EnableAsync
class AsyncConfig {

    @Bean("aiEmbeddingExecutor")
    fun aiEmbeddingExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 2
            maxPoolSize = 5
            queueCapacity = 100
            setThreadNamePrefix("ai-embedding-")
            setWaitForTasksToCompleteOnShutdown(true)
            setAwaitTerminationSeconds(60)
            initialize()
        }
    }

    @Bean("embeddingScheduler")
    fun embeddingScheduler(): ScheduledExecutorService {
        val threadFactory = object : ThreadFactory {
            private val counter = AtomicInteger(0)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "embedding-scheduler-${counter.incrementAndGet()}").apply {
                    isDaemon = true
                }
            }
        }
        return Executors.newScheduledThreadPool(2, threadFactory)
    }
}
