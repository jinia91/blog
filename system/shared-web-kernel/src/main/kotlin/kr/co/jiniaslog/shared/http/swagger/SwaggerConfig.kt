package kr.co.jiniaslog.shared.http.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.responses.ApiResponse
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import java.util.*
import java.util.stream.Collectors

@Configuration
class SwaggerConfig {

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
            val apiResponsesWithExceptions = getApiResponsesWithExceptions(handlerMethod)

            apiResponsesWithExceptions?.value?.forEach { apiResponseWithExceptions ->
                val description = extractDescription(apiResponseWithExceptions)
                val statusCode = apiResponseWithExceptions.httpStatusCode.value().toString()

                operation.responses.getOrPut(statusCode) { ApiResponse().description(description) }
                    .apply { this.description = "${this.description}<br>$description" }
            }
            operation
        }
    }

    private fun getApiResponsesWithExceptions(handlerMethod: HandlerMethod) =
        handlerMethod.getMethodAnnotation(ExceptionsApiResponses::class.java)

    private fun extractDescription(apiResponseWithExceptions: ExceptionApiResponse): String =
        Arrays.stream(apiResponseWithExceptions.exceptions)
            .map { obj -> obj.simpleName }
            .collect(Collectors.joining("<br>"))
}
