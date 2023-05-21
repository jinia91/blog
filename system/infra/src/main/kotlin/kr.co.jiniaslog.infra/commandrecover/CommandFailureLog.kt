package kr.co.jiniaslog.infra.commandrecover

import kr.co.jiniaslog.shared.core.domain.Command
import kr.co.jiniaslog.shared.core.infra.ObjectMapperUtils
import org.aspectj.lang.JoinPoint
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime

data class CommandFailureLog(
    val id: Long,
    val useCaseExecutorClass: String,
    val method: String,
    val commandClass: String,
    val command: String,
    val stacktrace: String,
    val occurredAt: LocalDateTime,
) {
    companion object {
        fun newOne(id: Long, joinPoint: JoinPoint, command: Command, throwable: Throwable): CommandFailureLog {
            val useCaseExecutorClass = joinPoint.signature.declaringTypeName
            val method = joinPoint.signature.name
            return CommandFailureLog(
                id,
                useCaseExecutorClass,
                method,
                command.javaClass.name,
                ObjectMapperUtils.defaultMapper.writeValueAsString(command),
                getStacktrace(throwable),
                LocalDateTime.now(),
            )
        }

        private fun getStacktrace(throwable: Throwable): String {
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            return sw.toString()
        }
    }
}
