package kr.co.jiniaslog

import kr.co.jiniaslog.shared.core.context.CustomComponent
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@ComponentScan(
    includeFilters = [
        ComponentScan.Filter(type = FilterType.ANNOTATION, value = [CustomComponent::class]),
    ]
)
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableScheduling
class App

fun main(args: Array<String>) {
    val application = SpringApplication(App::class.java)
    application.addInitializers(CustomAutoConfigYmlImportInitializer())
    application.run(*args)
}

