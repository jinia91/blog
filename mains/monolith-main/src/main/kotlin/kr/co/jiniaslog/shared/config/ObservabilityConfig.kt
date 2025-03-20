package kr.co.jiniaslog.shared.config

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationPredicate
import io.micrometer.observation.ObservationRegistry
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObservabilityConfig {
    @Bean
    fun noSpringSecurityObservations(): ObservationRegistryCustomizer<ObservationRegistry> {
        val predicate = ObservationPredicate { name: String, _: Observation.Context? ->
            !name.startsWith("spring.security.")
        }
        return ObservationRegistryCustomizer { registry: ObservationRegistry ->
            registry.observationConfig().observationPredicate(predicate)
        }
    }
}
