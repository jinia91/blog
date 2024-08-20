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
        if (span.tags["filtered"] == "true") {
            return@SpanExportingPredicate false
        }

        span.tags["http.url"]?.let {
            val shouldFilter = it.startsWith("/actuator") || it.startsWith("/ws")
            if (shouldFilter) {
                span.tags["filtered"] = "true"
                return@SpanExportingPredicate false
            }
        }
        true
    }
}
