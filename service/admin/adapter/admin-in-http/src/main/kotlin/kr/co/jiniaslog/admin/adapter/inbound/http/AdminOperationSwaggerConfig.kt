package kr.co.jiniaslog.admin.adapter.inbound.http

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!prod")
class AdminOperationSwaggerConfig {
    @Bean
    fun adminApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Admin Operation")
            .packagesToScan("kr.co.jiniaslog.admin.adapter.inbound.http")
            .build()
    }
}
