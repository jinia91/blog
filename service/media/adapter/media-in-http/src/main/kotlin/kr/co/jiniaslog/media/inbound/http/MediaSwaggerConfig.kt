package kr.co.jiniaslog.media.inbound.http

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MediaSwaggerConfig {
    @Bean
    fun mediaApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group(MEDIA_API)
            .packagesToScan(MEDIA_API_DTO_PATH)
            .build()
    }

    companion object {
        const val MEDIA_API = "MediaResource"
        const val MEDIA_API_DTO_PATH = "kr.co.jiniaslog.media.inbound.http"
    }
}
