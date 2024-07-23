package kr.co.jiniaslog.user.adapter.inbound.http

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserAuthSwaggerConfig {
    @Bean
    fun usrAuthApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("UserAuth Resource")
            .packagesToScan("kr.co.jiniaslog.user.adapter.inbound.http")
            .build()
    }
}
