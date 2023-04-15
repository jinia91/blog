package kr.co.jiniaslog

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val info: Info = Info()
            .title("Jinia's Log API")
            .version("1.0")
            .description("jini's Log API")
        return OpenAPI()
            .components(Components())
            .info(info)
    }
}