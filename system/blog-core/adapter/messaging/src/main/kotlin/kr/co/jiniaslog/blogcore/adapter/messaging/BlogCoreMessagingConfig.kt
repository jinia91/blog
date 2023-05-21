package kr.co.jiniaslog.blogcore.adapter.messaging

import kr.co.jiniaslog.shared.messaging.IdempotentReceiverFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BlogCoreMessagingConfig(
    private val idempotentReceiverFactory: IdempotentReceiverFactory,
) {

    @Bean
    fun blogCoreIdempotentReceiverInterceptor() =
        idempotentReceiverFactory.buildIdempotentReceiverInterceptor("blogCore")
}
