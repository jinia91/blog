package kr.co.jiniaslog.shared.config

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = mu.KotlinLogging.logger { }

@RestControllerAdvice
class RestExceptionGlobalAdvisor {
    @ExceptionHandler(value = [IllegalArgumentException::class])
    fun illegalArgumentExceptionHandler(e: IllegalArgumentException): ResponseEntity<Any> {
        log.error { e.stackTraceToString() }
        return ResponseEntity.status(400).body(e.message)
    }
}
