package kr.co.jiniaslog
import kr.co.jiniaslog.shared.core.domain.BusinessException
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kr.co.jiniaslog.shared.core.domain.ValidationException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger { }

@RestControllerAdvice
class GlobalRestControllerExceptionAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(e: ValidationException): ResponseEntity<ErrorResponse> {
        if (e.isNecessaryToLog) {
            log.error(e.message)
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(e.javaClass.simpleName ,e.message ?: "Validation Error"))
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = e.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to fieldError.defaultMessage
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(e.javaClass.simpleName, errors.toString()))
    }

    @ExceptionHandler(
        IllegalArgumentException::class,
        MissingServletRequestParameterException::class,
        HttpRequestMethodNotSupportedException::class,
        MissingRequestHeaderException::class,
        HttpMessageNotReadableException::class,
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(e.javaClass.simpleName, e.message ?: "Bad Request"))
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFoundException(e: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        if (e.isNecessaryToLog) {
            log.error(e.message)
        }
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(e.javaClass.simpleName ,e.message ?: "Resource Not Found"))
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        if (e.isNecessaryToLog) {
            log.error(e.message)
        }
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(e.javaClass.simpleName, e.message ?: "Business Error"))
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleThrowable(e: Throwable): ResponseEntity<ErrorResponse> {
        log.error { e.message }
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(e.javaClass.simpleName, e.message ?: "Unknown Error"))
    }
}

data class ErrorResponse(
    val type : String,
    val message: String
)