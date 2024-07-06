package kr.co.jiniaslog.memo.adapter.inbound.websocket

import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice

private val log = mu.KotlinLogging.logger {}

@ControllerAdvice
class ExceptionAdvisor {
    @MessageExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception) {
        // todo
    }
}
