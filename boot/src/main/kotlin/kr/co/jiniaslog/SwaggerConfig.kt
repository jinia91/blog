package kr.co.jiniaslog

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.responses.ApiResponse
import kr.co.jiniaslog.blogcore.adapter.http.config.ExceptionApiResponse
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.HandlerMethod
import java.util.*
import java.util.stream.Collectors


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

    @Bean
    fun apiNotFoundResponseCustomizer(): OperationCustomizer? {
        return OperationCustomizer { operation: Operation, handlerMethod: HandlerMethod ->
            val apiResponseWithExceptions: ExceptionApiResponse? = handlerMethod.getMethodAnnotation(
                ExceptionApiResponse::class.java
            )
            if (apiResponseWithExceptions != null) {
                val description: String = Arrays.stream(apiResponseWithExceptions.exceptions)
                    .map { obj -> obj.simpleName }
                    .collect(Collectors.joining("<br>"))

                val statusCode = apiResponseWithExceptions.httpStatusCode.value().toString()
                operation.responses.getOrPut(statusCode) { ApiResponse().description(description) }
                    .apply { this.description = "${this.description}<br>$description" }
            }
            operation
        }
    }
}