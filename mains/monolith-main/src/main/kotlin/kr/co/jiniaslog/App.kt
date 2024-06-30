package kr.co.jiniaslog

import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@SpringBootApplication
class App

@ComponentScan(
    includeFilters = [
        ComponentScan.Filter(type = FilterType.ANNOTATION, value = [CustomComponent::class]),
    ],
)
@Configuration
@ConfigurationPropertiesScan
class ComponentScanConfig

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
