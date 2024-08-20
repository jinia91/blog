package kr.co.jiniaslog.shared.config

import io.micrometer.tracing.exporter.SpanExportingPredicate
import org.springframework.boot.actuate.autoconfigure.tracing.otlp.OtlpProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObservationConfig(
    private val otlpProperties: OtlpProperties,
) {
    @Bean
    fun spanExportingPredicate(): SpanExportingPredicate = SpanExportingPredicate { span ->
        span.tags["http.url"]?.let {
            val shouldFilter = it.startsWith("/actuator")
            if (shouldFilter) {
                return@SpanExportingPredicate false
            }
        }
        true
    }
}
