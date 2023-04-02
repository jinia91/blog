package kr.co.jiniaslog

import kr.co.jiniaslog.lib.context.AntiCorruptLayer
import kr.co.jiniaslog.lib.context.CustomComponent
import kr.co.jiniaslog.lib.context.DomainService
import kr.co.jiniaslog.lib.context.UseCaseInteractor
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@ComponentScan(
    includeFilters = [
        ComponentScan.Filter(type = FilterType.ANNOTATION, value = [CustomComponent::class]),
    ]
)
@SpringBootApplication
@ConfigurationPropertiesScan
class App

fun main(args: Array<String>) {
    val application = SpringApplication(App::class.java)
    application.addInitializers(CustomAutoConfigYmlImportInitializer())
    application.run(*args)
}

