package kr.co.jiniaslog

import kr.co.jiniaslog.lib.context.DomainService
import kr.co.jiniaslog.lib.context.UseCaseInteractor
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScans
import org.springframework.context.annotation.FilterType

@SpringBootApplication
@ComponentScan(
    includeFilters = [
        ComponentScan.Filter(type = FilterType.ANNOTATION, value = [UseCaseInteractor::class]),
        ComponentScan.Filter(type = FilterType.ANNOTATION, value = [DomainService::class]),
    ]
)
class App


fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, *args)
}

