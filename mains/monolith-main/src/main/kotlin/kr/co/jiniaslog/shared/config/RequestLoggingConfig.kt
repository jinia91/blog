package kr.co.jiniaslog.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
class RequestLoggingConfig {
    @Bean
    fun logFilter(): CommonsRequestLoggingFilter {
        val filter = CommonsRequestLoggingFilter()
        filter.setIncludeClientInfo(true)
        filter.setIncludeQueryString(true)
        filter.setIncludePayload(true)
        filter.setIncludeHeaders(true)
        filter.setMaxPayloadLength(10000)
        return filter
    }
}
