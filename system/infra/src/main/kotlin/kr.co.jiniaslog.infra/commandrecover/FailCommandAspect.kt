package kr.co.jiniaslog.infra.commandrecover

import kr.co.jiniaslog.shared.core.domain.Command
import kr.co.jiniaslog.shared.core.domain.IdGenerator
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Aspect
@Component
class FailCommandAspect(
    private val storage: CommandFailureLogStore,
    private val idGenerator: IdGenerator,
) {
    @AfterThrowing(
        pointcut = "within(@kr.co.jiniaslog.shared.core.context.UseCaseInteractor *) && execution(* *(..)) && args(command,..)",
        throwing = "throwable",
    )
    fun logCommandFailure(joinPoint: JoinPoint, command: Command, throwable: Throwable) {
        val commandFailureLog: CommandFailureLog =
            CommandFailureLog.newOne(idGenerator.generate(), joinPoint, command, throwable)
        storage.store(commandFailureLog)
    }
}
