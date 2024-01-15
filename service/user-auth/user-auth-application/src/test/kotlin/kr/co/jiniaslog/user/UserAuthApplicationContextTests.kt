package kr.co.jiniaslog.user

import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@ComponentScan(
    includeFilters = [
        ComponentScan.Filter(type = FilterType.ANNOTATION, value = [CustomComponent::class]),
    ],
)
@SpringBootApplication
@ConfigurationPropertiesScan
class UserAuthApp

@SpringBootTest
class UserAuthApplicationContextTests {
    @Test
    fun contextLoads() {}
}
