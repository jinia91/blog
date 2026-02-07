package kr.co.jiniaslog.ai.adapter.inbound.http

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AiSwaggerConfig {
    @Bean
    fun aiApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("AI Second Brain")
            .packagesToScan("kr.co.jiniaslog.ai.adapter.inbound.http")
            .build()
    }
}
